
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
            throw new IllegalArgumentException("undoLimit must be ≥ -1");
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
        this.maxWidth  = map.keySet().stream().mapToInt(Position::x).max().orElse(-1) + 1;
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
        String[] lines = mapText.strip().split("\\R");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map text must have at least undo limit and one row");
        }

        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid undo limit: " + lines[0].trim(), ex);
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("undoLimit must be ≥ -1");
        }

        Map<Position, Entity> map       = new HashMap<>();
        Set<Position>        dests      = new HashSet<>();
        Map<Integer, AtomicInteger> boxesByPlayer = new HashMap<>();
        Set<Integer>         players    = new HashSet<>();

        for (int y = 1; y < lines.length; y++) {
            String row = lines[y];
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                Position p = new Position(x, y - 1);
                Entity e;
                switch (c) {
                    case '#':
                        e = new Wall();
                        break;
                    case '@':
                        dests.add(p);
                        e = new Empty();
                        break;
                    case '.':
                        e = new Empty();
                        break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int pid = c - 'A';
                            if (!players.add(pid)) {
                                throw new IllegalArgumentException("Duplicate player ID: " + c);
                            }
                            e = new Player(pid);
                        } else if (Character.isLowerCase(c)) {
                            int pid = c - 'a';
                            boxesByPlayer.computeIfAbsent(pid, k -> new AtomicInteger(0)).incrementAndGet();
                            e = new Box(pid);
                        } else {
                            throw new IllegalArgumentException("Unrecognized map character: " + c);
                        }
                }
                map.put(p, e);
            }
        }

        if (players.isEmpty()) {
            throw new IllegalArgumentException("At least one player required");
        }
        int totalBoxes = boxesByPlayer.values().stream().mapToInt(AtomicInteger::get).sum();
        if (totalBoxes != dests.size()) {
            throw new IllegalArgumentException("Number of boxes (" + totalBoxes +
                                               ") must equal number of destinations (" + dests.size() + ")");
        }
        if (!boxesByPlayer.keySet().equals(players)) {
            throw new IllegalArgumentException("Mismatch between players and boxes: players=" +
                                               players + ", boxOwners=" + boxesByPlayer.keySet());
        }

        return new GameMap(map, dests, undoLimit);
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
        @SuppressWarnings("unchecked")
        Map<Position, Entity> m = (Map<Position, Entity>) this.map;
        m.put(position, entity);
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
     * @return empty if undo not allowed (0), otherwise the limit (including unlimited = -1).
     */
    public Optional<Integer> getUndoLimit() {
        return undoLimit == 0
             ? Optional.empty()
             : Optional.of(undoLimit);
    }

    /**
     * Get all players' id as a set.
     *
     * @return a set of player id.
     */
    public Set<Integer> getPlayerIds() {
        Set<Integer> ids = new HashSet<>();
        for (Entity e : map.values()) {
            if (e instanceof Player p) {
                ids.add(p.getId());
            }
        }
        return Collections.unmodifiableSet(ids);
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