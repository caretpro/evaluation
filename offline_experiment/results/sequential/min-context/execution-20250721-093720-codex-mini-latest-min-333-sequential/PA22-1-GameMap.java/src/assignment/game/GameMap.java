
package assignment.game;

import assignment.entities.Entity;

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
     * Get the maximum height of the game map.
     *
     * @return maximum height.
     */
    public int getMaxHeight() {
        return maxHeight;
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
     * Get the entity object at the given position.
     *
     * @param position the position of the entity in the game map.
     * @return Entity object.
     * @throws IllegalArgumentException if the position is outside the map or no entity exists at that position.
     */
    public Entity getEntity(Position position) {
        Objects.requireNonNull(position, "position must not be null");
        if (position.x() < 0 || position.y() < 0 || position.x() >= maxWidth || position.y() >= maxHeight) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        Entity e = map.get(position);
        if (e == null) {
            throw new IllegalArgumentException("No entity at position: " + position);
        }
        return e;
    }

    /**
     * Put one entity at the given position in the game map.
     *
     * @param position the position in the game map to put the entity.
     * @param entity   the entity to put into game map.
     */
    public void putEntity(Position position, Entity entity) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(entity, "entity must not be null");
        if (position.x() < 0 || position.y() < 0 || position.x() >= maxWidth || position.y() >= maxHeight) {
            throw new IllegalArgumentException("Position out of bounds: " + position);
        }
        // we know this.map is a modifiable HashMap when created via first ctor:
        ((HashMap<Position, Entity>) map).put(position, entity);
    }

    /**
     * Get all box destination positions as a set in the game map.
     *
     * @return a set of positions.
     */
    public Set<Position> getDestinations() {
        return destinations;
    }

    /**
     * Get the undo limit of the game map.
     *
     * @return undo limit.
     */
    public Optional<Integer> getUndoLimit() {
        return undoLimit < 0 ? Optional.empty() : Optional.of(undoLimit);
    }

    /**
     * Get all players' id as a set.
     *
     * @return a set of player id.
     */
    public Set<Integer> getPlayerIds() {
        return map.values().stream().filter(e -> e instanceof assignment.entities.Player)
                .map(e -> ((assignment.entities.Player) e).getId())
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Parse a textual map into a GameMap.
     *
     * @param mapText multiline text, first line undo-limit, following lines the grid.
     * @return the parsed GameMap.
     */
    public static GameMap parse(String mapText) {
        Objects.requireNonNull(mapText, "mapText must not be null");
        String[] lines = mapText.split("\\R", -1);
        if (lines.length < 2) {
            throw new IllegalArgumentException("mapText must have at least undo‑limit line and one map line");
        }

        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be an integer (undo limit)", e);
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("undo limit must be >= -1");
        }

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Integer, Position> players = new HashMap<>();
        Map<Integer, List<Position>> boxes = new HashMap<>();

        int height = lines.length - 1;
        int width = Arrays.stream(lines, 1, lines.length).mapToInt(String::length).max().orElse(0);

        for (int y = 0; y < height; y++) {
            String row = lines[y + 1];
            for (int x = 0; x < width; x++) {
                char c = x < row.length() ? row.charAt(x) : '#';
                Position pos = new Position(x, y);
                switch (c) {
                    case '#':
                        map.put(pos, new assignment.entities.Wall());
                        break;
                    case '.':
                        map.put(pos, new assignment.entities.Empty());
                        break;
                    case '@':
                        map.put(pos, new assignment.entities.Empty());
                        destinations.add(pos);
                        break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int pid = c - 'A';
                            if (players.containsKey(pid)) {
                                throw new IllegalArgumentException("Duplicate player '" + c + "'");
                            }
                            players.put(pid, pos);
                            map.put(pos, new assignment.entities.Player(pid));
                        } else if (Character.isLowerCase(c)) {
                            int pid = c - 'a';
                            boxes.computeIfAbsent(pid, k -> new ArrayList<>()).add(pos);
                            map.put(pos, new assignment.entities.Box(pid));
                        } else {
                            throw new IllegalArgumentException("Invalid map character: '" + c + "'");
                        }
                }
            }
        }

        if (players.isEmpty()) {
            throw new IllegalArgumentException("At least one player required");
        }
        if (boxes.values().stream().mapToInt(List::size).sum() != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes must equal number of destinations");
        }
        for (int pid : boxes.keySet()) {
            if (!players.containsKey(pid)) {
                throw new IllegalArgumentException("Box for playerId=" + pid + " has no matching player");
            }
        }
        for (int pid : players.keySet()) {
            if (!boxes.containsKey(pid)) {
                throw new IllegalArgumentException("Player " + (char) ('A' + pid) + " has no corresponding box");
            }
        }

        return new GameMap(map, destinations, undoLimit);
    }
}