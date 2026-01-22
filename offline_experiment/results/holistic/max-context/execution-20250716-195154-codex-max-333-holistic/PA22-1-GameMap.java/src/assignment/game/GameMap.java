
package assignment.game;

import assignment.actions.Move;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A Sokoban game board.
 * GameBoard consists of information loaded from map data, such as
 * <li>Width and height of the game map</li>
 * <li>Walls in the map</li>
 * <li>Box destinations</li>
 * <li>Initial locations of boxes and player</li>
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
            throw new IllegalArgumentException("Undo limit must be ≥ -1");
        }
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = Collections.unmodifiableMap(new HashMap<>(map));
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(-1) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(-1) + 1;
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
        String[] lines = mapText.split("\\R");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map text must have at least undo-limit line and one map line");
        }

        // parse undo limit
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be an integer undo limit", e);
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit must be ≥ -1");
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Integer, Position> players = new HashMap<>();
        List<Box> boxes = new ArrayList<>();

        for (int y = 1; y < lines.length; y++) {
            String row = lines[y];
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                Position pos = Position.of(x, y - 1);
                switch (c) {
                    case '#':
                        map.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        map.put(pos, new Empty());
                        break;
                    case '.':
                        map.put(pos, new Empty());
                        break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int pid = c - 'A';
                            if (players.containsKey(pid)) {
                                throw new IllegalArgumentException("Duplicate player " + c);
                            }
                            players.put(pid, pos);
                            map.put(pos, new Player(pid));
                        } else if (Character.isLowerCase(c)) {
                            int bid = c - 'a';
                            Box b = new Box(bid);
                            boxes.add(b);
                            map.put(pos, b);
                        } else {
                            throw new IllegalArgumentException("Invalid map char '" + c + "' at " + x + "," + (y - 1));
                        }
                }
            }
        }

        if (players.isEmpty()) {
            throw new IllegalArgumentException("Must have at least one player");
        }
        if (boxes.size() != destinations.size()) {
            throw new IllegalArgumentException(
                    "Box count (" + boxes.size() + ") must equal destination count (" + destinations.size() + ")");
        }

        // validate matching players & boxes
        Set<Integer> playerIds = players.keySet();
        Set<Integer> boxIds = boxes.stream().map(Box::getPlayerId).collect(Collectors.toSet());
        if (!playerIds.equals(boxIds)) {
            Set<Integer> onlyBoxes = new HashSet<>(boxIds);
            onlyBoxes.removeAll(playerIds);
            Set<Integer> onlyPlayers = new HashSet<>(playerIds);
            onlyPlayers.removeAll(boxIds);
            throw new IllegalArgumentException("Player/Box mismatch: extra boxes " + onlyBoxes + ", extra players " + onlyPlayers);
        }

        GameMap gm = new GameMap(map, destinations, undoLimit);
        return gm;
    }

    /**
     * Get the entity object at the given position.
     *
     * @param position the position of the entity in the game map.
     * @return Entity object.
     */
    public Entity getEntity(Position position) {
        return map.getOrDefault(position, new Empty());
    }

    /**
     * Put one entity at the given position in the game map.
     *
     * @param position the position in the game map to put the entity.
     * @param entity   the entity to put into game map.
     */
    public void putEntity(Position position, Entity entity) {
        Objects.requireNonNull(position);
        Objects.requireNonNull(entity);
        if (position.x() < 0 || position.x() >= maxWidth
         || position.y() < 0 || position.y() >= maxHeight) {
            throw new IndexOutOfBoundsException("Position out of game map bounds: " + position.x() + "," + position.y());
        }
        map.put(position, entity);
    }

    /**
     * Get all box destination positions as a set in the game map.
     *
     * @return a set of positions.
     */
    @Unmodifiable
    public Set<Position> getDestinations() {
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