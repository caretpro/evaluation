
package assignment.game;

import assignment.actions.Move;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Optional;

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
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = Collections.unmodifiableMap(map);
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
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

    /**
     * Parses the map from a string representation. The first line is undo limit.
     * Starting from the second line, the game map is represented as follows,
     * <li># represents a {@link Wall}</li>
     * <li>@ represents a box destination.</li>
     * <li>Any upper‑case letter represents a {@link Player}.</li>
     * <li>Any lower‑case letter represents a {@link Box} that is only movable by the
     * player with the corresponding upper‑case letter. For instance, box "a" can only
     * be moved by player "A" and not movable by player "B".</li>
     * <li>. represents an {@link Empty} position in the map.</li>
     * <li>space represents an {@link Empty} position outside the boundary.</li>
     * <p>
     * Notes:
     * <li>There can be at most 26 players. All implementations of classes in the
     * assignment.game package should support up to 26 players.</li>
     * <li>For simplicity, we assume the given map is bounded with a closed boundary.
     * There is no need to check this point.</li>
     * <li>Example maps can be found in "src/main/resources".</li>
     *
     * @param mapText The string representation.
     * @return The parsed GameMap object.
     * @throws IllegalArgumentException if undo limit is negative but not -1.
     * @throws IllegalArgumentException if there are multiple same upper-case letters.
     * @throws IllegalArgumentException if there are no players in the map.
     * @throws IllegalArgumentException if the number of boxes is not equal to the number of
     *                                  box destinations.
     * @throws IllegalArgumentException if there are boxes whose {@link Box#getPlayerId()}
     *                                  do not match any player, or if there are players
     *                                  with no corresponding boxes.
     */
    public static GameMap parse(String mapText) {
        String[] lines = mapText.stripTrailing().split("\\R");
        if (lines.length < 2) {
            throw new IllegalArgumentException("mapText must contain at least undo limit and one map row");
        }
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be an integer (undo limit)", e);
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit must be >= -1");
        }
        int height = lines.length - 1;
        int width = Arrays.stream(lines, 1, lines.length).mapToInt(String::length).max().orElse(0);

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        boolean[] hasPlayer = new boolean[26];
        int[] boxCount = new int[26];
        int totalBoxes = 0;

        for (int y = 0; y < height; y++) {
            String row = lines[y + 1];
            for (int x = 0; x < width; x++) {
                char c = (x < row.length() ? row.charAt(x) : ' ');
                Position pos = Position.of(x, y);
                switch (c) {
                    case '#':
                        map.put(pos, new Wall());
                        break;
                    case '@':
                        map.put(pos, new Empty());
                        destinations.add(pos);
                        break;
                    case '.':
                    case ' ':
                        map.put(pos, new Empty());
                        break;
                    default:
                        if (c >= 'A' && c <= 'Z') {
                            int pid = c - 'A';
                            if (hasPlayer[pid]) {
                                throw new IllegalArgumentException("Duplicate player '" + c + "'");
                            }
                            hasPlayer[pid] = true;
                            map.put(pos, new Player(pid));
                        } else if (c >= 'a' && c <= 'z') {
                            int pid = c - 'a';
                            boxCount[pid]++;
                            totalBoxes++;
                            map.put(pos, new Box(pid));
                        } else {
                            throw new IllegalArgumentException("Illegal map character '" + c + "'");
                        }
                }
            }
        }

        // ** Fix here: Arrays.stream(hasPlayer) is illegal. **
        int totalPlayers = (int) IntStream.range(0, hasPlayer.length)
                                          .filter(pid -> hasPlayer[pid])
                                          .count();
        if (totalPlayers == 0) {
            throw new IllegalArgumentException("Map must contain at least one player");
        }
        if (totalBoxes != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes (" + totalBoxes
                                               + ") does not match number of destinations (" + destinations.size() + ")");
        }
        for (int pid = 0; pid < 26; pid++) {
            if (boxCount[pid] > 0 && !hasPlayer[pid]) {
                throw new IllegalArgumentException("Box '" + (char)('a' + pid) + "' has no matching player");
            }
            if (hasPlayer[pid] && boxCount[pid] == 0) {
                throw new IllegalArgumentException("Player '" + (char)('A' + pid) + "' has no boxes");
            }
        }
        return new GameMap(map, destinations, undoLimit);
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
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(entity, "entity must not be null");
        map.put(position, entity);
    }

    /**
     * Get all box destination positions as a set in the game map.
     *
     * @return a set of positions.
     */
    public @NotNull @Unmodifiable Set<Position> getDestinations() {
        return destinations;
    }

    /**
     * Get the undo limit of the game map.
     *
     * @return undo limit, or empty if undo is unlimited.
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
        return Collections.unmodifiableSet(
            map.values().stream()
               .filter(e -> e instanceof Player)
               .map(e -> ((Player) e).getPlayerId())
               .collect(Collectors.toSet())
        );
    }
}