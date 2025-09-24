
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
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
        if (maxWidth <= 0 || maxHeight <= 0) {
            throw new IllegalArgumentException("Map dimensions must be positive");
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit cannot be less than -1");
        }
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();

        // Initialize every position to Empty (or Wall if in destinations)
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                Position p = new Position(x, y);
                this.map.put(p, destinations.contains(p) ? new Empty() : new Empty());
            }
        }
    }

    private GameMap(
        Map<Position, Entity> workingMap,
        Set<Position> destinations,
        int undoLimit,
        int width,
        int height
    ) {
        this.maxWidth  = width;
        this.maxHeight = height;
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.map = Collections.unmodifiableMap(new HashMap<>(workingMap));

        // Ensure all positions are present
        Map<Position, Entity> complete = new HashMap<>(this.map);
        for (int y = 0; y < maxHeight; y++) {
            for (int x = 0; x < maxWidth; x++) {
                Position p = new Position(x, y);
                complete.putIfAbsent(p, new Empty());
            }
        }
        this.map = Collections.unmodifiableMap(complete);
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
        Objects.requireNonNull(mapText, "mapText must not be null");
        String[] lines = mapText.lines().toArray(String[]::new);
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map text must contain at least undo limit and one map row");
        }

        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be an integer undo limit");
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit cannot be less than -1");
        }

        int height = lines.length - 1;
        int width  = Arrays.stream(lines, 1, lines.length)
                           .mapToInt(String::length)
                           .max().orElse(0);

        Map<Position, Entity> workingMap = new HashMap<>();
        Set<Position> dests = new HashSet<>();
        Map<Integer, Position> players = new HashMap<>();
        List<Box> boxes = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            String row = lines[y + 1];
            for (int x = 0; x < width; x++) {
                char c = x < row.length() ? row.charAt(x) : '.';
                Position p = new Position(x, y);
                Entity ent;
                switch (c) {
                    case '#':
                        ent = new Wall();
                        break;
                    case '@':
                        ent = new Empty();
                        dests.add(p);
                        break;
                    case '.':
                        ent = new Empty();
                        break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int pid = c - 'A';
                            if (players.put(pid, p) != null) {
                                throw new IllegalArgumentException("Duplicate player " + c);
                            }
                            ent = new Player(pid);
                        } else if (Character.isLowerCase(c)) {
                            int pid = Character.toUpperCase(c) - 'A';
                            Box b = new Box(pid);
                            boxes.add(b);
                            ent = b;
                        } else {
                            throw new IllegalArgumentException("Invalid map character: " + c);
                        }
                }
                workingMap.put(p, ent);
            }
        }

        if (players.isEmpty()) {
            throw new IllegalArgumentException("No players on the map");
        }
        if (boxes.size() != dests.size()) {
            throw new IllegalArgumentException("Number of boxes (" + boxes.size() +
                                               ") must equal number of destinations (" + dests.size() + ")");
        }

        Set<Integer> boxOwners  = boxes.stream().map(Box::getPlayerId).collect(Collectors.toSet());
        Set<Integer> playerIds  = players.keySet();
        if (!playerIds.equals(boxOwners)) {
            Set<Integer> missing = new HashSet<>(playerIds);
            missing.removeAll(boxOwners);
            Set<Integer> extra   = new HashSet<>(boxOwners);
            extra.removeAll(playerIds);
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException("Players without boxes: " + missing);
            }
            if (!extra.isEmpty()) {
                throw new IllegalArgumentException("Boxes without players: " + extra);
            }
        }

        return new GameMap(workingMap, dests, undoLimit, width, height);
    }

    /**
     * Get the entity object at the given position.
     *
     * @param position the position of the entity in the game map.
     * @return Entity object.
     */
    public Entity getEntity(Position position) {
        return map.get(position);
    }

    /**
     * Put one entity at the given position in the game map.
     *
     * @param position the position in the game map to put the entity.
     * @param entity   the entity to put into game map.
     */
    public void putEntity(Position position, Entity entity) {
        if (position.x() < 0 || position.x() >= maxWidth ||
            position.y() < 0 || position.y() >= maxHeight) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
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