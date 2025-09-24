
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
 * GameMap consists of information loaded from map data, such as
 * <li>Width and height of the game map</li>
 * <li>Walls in the map</li>
 * <li>Box destinations</li>
 * <li>Initial locations of boxes and player</li>
 * <p/>
 * GameMap is capable to create many GameState instances, each representing an ongoing game.
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
            throw new IllegalArgumentException("Invalid undo limit: " + undoLimit);
        }
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;

        // Initialize the map: fill every cell with Empty, then override destinations as Empty too
        Map<Position, Entity> built = new HashMap<>(maxWidth * maxHeight);
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                Position p = new Position(x, y);
                Empty e = new Empty();
                e.setPosition(p);
                built.put(p, e);
            }
        }
        for (Position dest : destinations) {
            Empty e = new Empty();
            e.setPosition(dest);
            built.put(dest, e);
        }
        this.map = Collections.unmodifiableMap(built);
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = Collections.unmodifiableMap(new HashMap<>(map));
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
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
     * <li>There can be at most 26 players.</li>
     * <li>No need to check boundary closure.</li>
     * <li>Example maps in "src/main/resources".</li>
     *
     * @param mapText The string representation.
     * @return The parsed GameMap object.
     * @throws IllegalArgumentException on invalid undo limit, duplicate players, missing players,
     *                                  box/destination count mismatch, or unmatched player/box IDs.
     */
    public static GameMap parse(String mapText) {
        String[] lines = mapText.strip().split("\\R");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map must have at least undo limit and one row");
        }

        // parse undo limit
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit: " + lines[0]);
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit must be >= -1");
        }

        Map<Position, Entity> built = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Integer, Position> playerPos = new HashMap<>();
        List<Box> boxes = new ArrayList<>();

        int height = lines.length - 1;
        int width = Arrays.stream(lines, 1, lines.length)
                          .mapToInt(String::length)
                          .max()
                          .orElse(0);

        for (int y = 0; y < height; y++) {
            String row = lines[y + 1];
            for (int x = 0; x < width; x++) {
                char c = x < row.length() ? row.charAt(x) : '#';
                Position pos = new Position(x, y);
                switch (c) {
                    case '#' -> {
                        Wall w = new Wall();
                        w.setPosition(pos);
                        built.put(pos, w);
                    }
                    case '@' -> {
                        destinations.add(pos);
                        Empty e = new Empty();
                        e.setPosition(pos);
                        built.put(pos, e);
                    }
                    case '.' -> {
                        Empty e = new Empty();
                        e.setPosition(pos);
                        built.put(pos, e);
                    }
                    default -> {
                        if (Character.isUpperCase(c)) {
                            int pid = c - 'A';
                            if (playerPos.putIfAbsent(pid, pos) != null) {
                                throw new IllegalArgumentException("Duplicate player: " + c);
                            }
                            Player p = new Player();
                            p.setPosition(pos);
                            built.put(pos, p);
                        } else if (Character.isLowerCase(c)) {
                            int pid = c - 'a';
                            Box b = new Box();
                            b.setPosition(pos);
                            boxes.add(b);
                            built.put(pos, b);
                        } else {
                            throw new IllegalArgumentException("Invalid map character: " + c);
                        }
                    }
                }
            }
        }

        if (playerPos.isEmpty()) {
            throw new IllegalArgumentException("At least one player required");
        }
        if (boxes.size() != destinations.size()) {
            throw new IllegalArgumentException("Boxes and destinations counts differ");
        }

        Set<Integer> boxPids = boxes.stream()
                                    .map(Box::getPlayerId)
                                    .collect(Collectors.toSet());
        Set<Integer> playerPids = playerPos.keySet();
        if (!playerPids.equals(boxPids)) {
            throw new IllegalArgumentException(
                    "Players/boxes mismatch; players=" + playerPids + " boxes=" + boxPids);
        }

        return new GameMap(built, destinations, undoLimit);
    }

    /**
     * Get the entity object at the given position.
     *
     * @param position the position of the entity in the game map.
     * @return Entity object.
     */
    public @NotNull Entity getEntity(Position position) {
        Entity e = map.get(position);
        if (e == null) {
            throw new IllegalArgumentException("No cell at position " + position);
        }
        return e;
    }

    /**
     * GameMap is immutable after construction.
     */
    public void putEntity(Position position, Entity entity) {
        throw new UnsupportedOperationException("GameMap is immutable after construction");
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
     * @return undo limit (empty if unlimited).
     */
    public Optional<Integer> getUndoLimit() {
        return undoLimit < 0 ? Optional.empty() : Optional.of(undoLimit);
    }

    /**
     * Get all players' id as a set.
     *
     * @return a set of player ids.
     */
    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                  .filter(e -> e instanceof Player)
                  .map(e -> ((Player) e).getPlayerId())
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