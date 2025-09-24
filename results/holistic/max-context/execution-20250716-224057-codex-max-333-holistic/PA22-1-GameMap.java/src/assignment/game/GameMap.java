
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
        if (mapText == null || mapText.isEmpty()) {
            throw new IllegalArgumentException("mapText must not be null or empty");
        }
        List<String> lines = Arrays.stream(mapText.split("\\R"))
                                   .filter(l -> !l.isEmpty())
                                   .toList();
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("No content in mapText");
        }

        // 1) Parse undo limit
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines.get(0).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit: " + lines.get(0), e);
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit must be >= -1");
        }

        // 2) Determine grid dimensions
        int height = lines.size() - 1;
        int width = lines.stream()
                         .skip(1)
                         .mapToInt(String::length)
                         .max()
                         .orElse(0);

        // 3) Read the grid, overlaying specific entities
        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Integer, Position> players = new HashMap<>();
        Map<Integer, List<Position>> boxes = new HashMap<>();

        for (int y = 1; y <= height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < row.length(); x++) {
                Position pos = Position.of(x, y - 1);
                char c = row.charAt(x);
                switch (c) {
                    case '#' -> map.put(pos, new Wall());
                    case '@' -> {
                        map.put(pos, new Empty());
                        destinations.add(pos);
                    }
                    case '.' -> map.put(pos, new Empty());
                    default -> {
                        if (Character.isUpperCase(c)) {
                            int pid = c - 'A';
                            if (players.putIfAbsent(pid, pos) != null) {
                                throw new IllegalArgumentException("Duplicate player: " + c);
                            }
                            map.put(pos, new Player(pid));
                        } else if (Character.isLowerCase(c)) {
                            int pid = Character.toUpperCase(c) - 'A';
                            map.put(pos, new Box(pid));
                            boxes.computeIfAbsent(pid, k -> new ArrayList<>()).add(pos);
                        } else {
                            throw new IllegalArgumentException("Unknown map character: " + c);
                        }
                    }
                }
            }
        }

        // 4) Fill in any remaining spots as Empty
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                map.putIfAbsent(Position.of(x, y), new Empty());
            }
        }

        // 5) Validate players/boxes/destinations
        if (players.isEmpty()) {
            throw new IllegalArgumentException("No players defined in map");
        }
        if (boxes.values().stream().mapToInt(List::size).sum() != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes must equal number of destinations");
        }
        for (Integer pid : boxes.keySet()) {
            if (!players.containsKey(pid)) {
                throw new IllegalArgumentException("Box with no matching player: " + (char)('A' + pid));
            }
        }
        for (Integer pid : players.keySet()) {
            if (!boxes.containsKey(pid)) {
                throw new IllegalArgumentException("Player has no box: " + (char)('A' + pid));
            }
        }

        return new GameMap(map, destinations, undoLimit);
    }

    /** Get the Entity at a position (Empty if none). */
    public Entity getEntity(@NotNull Position position) {
        Objects.requireNonNull(position, "position");
        return map.getOrDefault(position, new Empty());
    }

    /** Put one Entity at a position (only allowed on the mutable base map). */
    public void putEntity(@NotNull Position position, @NotNull Entity entity) {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(entity, "entity");
        if (map instanceof HashMap<?, ?> hm) {
            @SuppressWarnings("unchecked")
            HashMap<Position, Entity> m = (HashMap<Position, Entity>) hm;
            m.put(position, entity);
        } else {
            throw new UnsupportedOperationException("Cannot modify immutable map");
        }
    }

    /** All box destinations (unmodifiable). */
    @Unmodifiable
    public Set<Position> getDestinations() {
        return destinations;
    }

    /** Undo limit (empty=unlimited). */
    public Optional<Integer> getUndoLimit() {
        return undoLimit == -1 ? Optional.empty() : Optional.of(undoLimit);
    }

    /** All player‑IDs present. */
    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                  .filter(e -> e instanceof Player)
                  .map(e -> ((Player)e).getPlayerId())
                  .collect(Collectors.toUnmodifiableSet());
    }

    /** Map width. */
    public int getMaxWidth() {
        return maxWidth;
    }

    /** Map height. */
    public int getMaxHeight() {
        return maxHeight;
    }
}