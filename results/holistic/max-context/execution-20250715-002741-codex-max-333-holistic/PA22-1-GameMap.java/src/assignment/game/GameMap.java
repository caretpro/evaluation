
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Sokoban game board.
 * GameBoard consists of information loaded from map data, such as
 * <li>Width and height of the game map</li>
 * <li>Walls in the map</li>
 * <li>Box destinations</li>
 * <li>Initial locations of boxes and players</li>
 * <p/>
 * GameBoard is capable to create many GameState instances, each representing an ongoing game.
 */
public class GameMap {

    private final Map<Position, Entity> map;
    private final int maxWidth;
    private final int maxHeight;
    private final Set<Position> destinations;
    private final int undoLimit;

    /**
     * Create a new GameMap with width, height, set of box destinations and undo limit.
     *
     * @param maxWidth     Width of the game map.
     * @param maxHeight    Height of the game map.
     * @param destinations Set of box destination positions.
     * @param undoLimit    Undo limit.
     *                     Positive numbers specify the maximum number of undo actions.
     *                     0 means undo is not allowed.
     *                     -1 means unlimited. Other negative numbers are not allowed.
     */
    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Invalid undoLimit: " + undoLimit);
        }
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
        // initialize all cells as Empty
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                Position pos = Position.of(x, y);
                map.put(pos, new Empty());
            }
        }
    }

    private GameMap(Map<Position, Entity> filledMap, Set<Position> destinations, int undoLimit) {
        this.map = new HashMap<>(filledMap);
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.maxWidth = filledMap.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = filledMap.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    /**
     * Parses the map from a string representation.
     * The first line is undo limit.
     * Starting from the second line, the game map is represented as follows,
     * <li># represents a {@link Wall}</li>
     * <li>@ represents a box destination.</li>
     * <li>Any upper-case letter represents a {@link Player}.</li>
     * <li>
     * Any lower-case letter represents a {@link Box} that is only movable by the player with the corresponding upper-case letter.
     * For instance, box "a" can only be moved by player "A" and not movable by player "B".
     * </li>
     * <li>. represents an {@link Empty} position in the map, meaning there is no player or box currently at this position.</li>
     * <p>
     * Notes:
     * <li>
     * There can be at most 26 players.
     * All implementations of classes in the assignment.game package should support up to 26 players.
     * </li>
     * <li>
     * For simplicity, we assume the given map is bounded with a closed boundary.
     * There is no need to check this point.
     * </li>
     * <li>
     * Example maps can be found in "src/main/resources".
     * </li>
     *
     * @param mapText The string representation.
     * @return The parsed GameMap object.
     * @throws IllegalArgumentException if undo limit is negative but not -1.
     * @throws IllegalArgumentException if there are multiple same upper-case letters, i.e., one player can only exist at one position.
     * @throws IllegalArgumentException if there are no players in the map.
     * @throws IllegalArgumentException if the number of boxes is not equal to the number of box destinations.
     * @throws IllegalArgumentException if there are boxes whose {@link Box#getPlayerId()} do not match any player on the game board,
     *                                  or if there are players that have no corresponding boxes.
     */
    public static GameMap parse(String mapText) {
        String[] lines = mapText.strip().split("\\R");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Not enough lines for undo limit and map");
        }

        // Parse undo limit
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit: " + lines[0]);
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("undo limit must be >= -1");
        }

        int height = lines.length - 1;
        int width  = Arrays.stream(lines, 1, lines.length)
                           .mapToInt(String::length)
                           .max()
                           .orElse(0);

        Map<Position, Entity> grid = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Integer, Position> players = new HashMap<>();
        Map<Integer, List<Position>> boxesByPlayer = new HashMap<>();

        // initialize all cells as Empty
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid.put(Position.of(x, y), new Empty());
            }
        }

        // fill in walls, players, boxes, and destinations
        for (int y = 1; y < lines.length; y++) {
            String row = lines[y];
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                Position pos = Position.of(x, y - 1);
                switch (c) {
                    case '#' -> grid.put(pos, new Wall());
                    case '.' -> { /* already Empty */ }
                    case '@' -> {
                        destinations.add(pos);
                    }
                    default -> {
                        if (Character.isUpperCase(c)) {
                            int pid = c - 'A';
                            if (players.putIfAbsent(pid, pos) != null) {
                                throw new IllegalArgumentException("Duplicate player: " + c);
                            }
                            grid.put(pos, new Player(pid));
                        } else if (Character.isLowerCase(c)) {
                            int pid = Character.toUpperCase(c) - 'A';
                            boxesByPlayer.computeIfAbsent(pid, k -> new ArrayList<>()).add(pos);
                            grid.put(pos, new Box(pid));
                        } else {
                            throw new IllegalArgumentException("Unknown map char: " + c);
                        }
                    }
                }
            }
        }

        if (players.isEmpty()) {
            throw new IllegalArgumentException("No players found");
        }
        int totalBoxes = boxesByPlayer.values().stream().mapToInt(List::size).sum();
        if (totalBoxes != destinations.size()) {
            throw new IllegalArgumentException("Boxes (" + totalBoxes +
                                               ") != destinations (" + destinations.size() + ")");
        }
        for (int pid : boxesByPlayer.keySet()) {
            if (!players.containsKey(pid)) {
                throw new IllegalArgumentException("Box for missing player: " + (char)('A' + pid));
            }
        }
        for (int pid : players.keySet()) {
            if (!boxesByPlayer.containsKey(pid)) {
                throw new IllegalArgumentException("Player with no boxes: " + (char)('A' + pid));
            }
        }

        return new GameMap(grid, destinations, undoLimit);
    }

    /**
     * Get the entity object at the given position.
     *
     * @param position the position of the entity in the game map.
     * @return Entity object.
     */
    public Entity getEntity(@NotNull Position position) {
        Entity e = map.get(position);
        if (e == null) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        return e;
    }

    /**
     * Put one entity at the given position in the game map.
     *
     * @param position the position in the game map to put the entity.
     * @param entity   the entity to put into game map.
     */
    public void putEntity(@NotNull Position position, @NotNull Entity entity) {
        if (!map.containsKey(position)) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        map.put(position, entity);
    }

    /**
     * Get all box destination positions as an unmodifiable set in the game map.
     *
     * @return a set of positions.
     */
    public @Unmodifiable Set<Position> getDestinations() {
        return destinations;
    }

    /**
     * Get the undo limit of the game map.
     *
     * @return undo limit.
     */
    public Optional<Integer> getUndoLimit() {
        return undoLimit == -1 ? Optional.empty() : Optional.of(undoLimit);
    }

    /**
     * Get all players' id as a set.
     *
     * @return a set of player id.
     */
    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                  .filter(e -> e instanceof Player)
                  .map(e -> ((Player) e).getId())
                  .collect(Collectors.toSet());
    }

    /**
     * Get the maximum width of the game map.
     *
     * @return maximum width.
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Get the maximum height of the game map.
     *
     * @return maximum height.
     */
    public int getMaxHeight() {
        return maxHeight;
    }
}