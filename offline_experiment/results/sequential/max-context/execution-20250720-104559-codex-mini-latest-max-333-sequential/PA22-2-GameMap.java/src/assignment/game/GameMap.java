
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.NotNull;

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
            throw new IllegalArgumentException("Undo limit must be >= -1");
        }
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit must be >= -1");
        }
        this.map = Collections.unmodifiableMap(new HashMap<>(map));
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(-1) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(-1) + 1;
    }

    /**
     * Get all players' id as a set.
     *
     * @return a set of player id.
     */
    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                  .filter(e -> e instanceof Player)
                  .map(e -> ((Player) e).getPlayerId())
                  .collect(Collectors.toSet());
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
     * Parses the map from a string representation. The first line is undo limit. Starting from the second line, the game map is represented as follows,
     * <li># represents a  {@link Wall} </li>
     * <li>@ represents a box destination.</li>
     * <li>Any upper-case letter represents a  {@link Player} .</li>
     * <li> Any lower-case letter represents a  {@link Box}  that is only movable by the player with the corresponding upper-case letter.
     * For instance, box "a" can only be moved by player "A" and not movable by player "B". </li>
     * <li>. represents an  {@link Empty}  position in the map, meaning there is no player or box currently at this position.</li>
     * <p> Notes:
     * <li> There can be at most 26 players. All implementations of classes in the assignment.game package should support up to 26 players. </li>
     * <li> For simplicity, we assume the given map is bounded with a closed boundary. There is no need to check this point. </li>
     * <li> Example maps can be found in "src/main/resources". </li>
     *
     * @param mapText  The string representation.
     * @return  The parsed GameMap object.
     * @throws IllegalArgumentException  if undo limit is negative but not -1.
     * @throws IllegalArgumentException  if there are multiple same upper-case letters, i.e., one player can only exist at one position.
     * @throws IllegalArgumentException  if there are no players in the map.
     * @throws IllegalArgumentException  if the number of boxes is not equal to the number of box destinations.
     * @throws IllegalArgumentException  if there are boxes whose  {@link Box#getPlayerId()}  do not match any player on the game board,
     *                                   or if there are players that have no corresponding boxes.
     */
    public static GameMap parse(String mapText) {
        Objects.requireNonNull(mapText, "mapText must not be null");
        String[] lines = mapText.split("\\r?\\n", -1);
        if (lines.length < 2) {
            throw new IllegalArgumentException("mapText must contain at least undo limit line and one row of the map");
        }
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit: " + lines[0], e);
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit must be >= -1");
        }

        int height = lines.length - 1;
        int width  = Arrays.stream(lines, 1, lines.length)
                           .mapToInt(String::length)
                           .max().orElse(0);

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Character, Position> playerPos = new HashMap<>();
        Map<Character, List<Position>> boxPos = new HashMap<>();

        for (int row = 0; row < height; row++) {
            String line = lines[row + 1];
            for (int col = 0; col < width; col++) {
                char ch = col < line.length() ? line.charAt(col) : ' ';
                Position pos = Position.of(col, row);
                switch (ch) {
                    case '#': map.put(pos, new Wall());     break;
                    case '@': map.put(pos, new Empty()); destinations.add(pos); break;
                    case '.': map.put(pos, new Empty());    break;
                    default:
                        if (ch >= 'A' && ch <= 'Z') {
                            if (playerPos.put(ch, pos) != null) {
                                throw new IllegalArgumentException("Duplicate player '" + ch + '\'');
                            }
                            map.put(pos, new Player(ch - 'A'));
                        } else if (ch >= 'a' && ch <= 'z') {
                            char up = Character.toUpperCase(ch);
                            boxPos.computeIfAbsent(up, __ -> new ArrayList<>()).add(pos);
                            map.put(pos, new Box(ch - 'a'));
                        } else if (ch == ' ') {
                            map.put(pos, new Empty());
                        } else {
                            throw new IllegalArgumentException("Invalid map character: '" + ch + '\'');
                        }
                }
            }
        }
        if (playerPos.isEmpty()) {
            throw new IllegalArgumentException("No players found on the map");
        }
        int totalBoxes = boxPos.values().stream().mapToInt(List::size).sum();
        if (totalBoxes != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes (" + totalBoxes +
                                               ") does not match number of destinations (" + destinations.size() + ")");
        }
        for (char playerCh : playerPos.keySet()) {
            if (!boxPos.containsKey(playerCh)) {
                throw new IllegalArgumentException("Player '" + playerCh + "' has no corresponding boxes");
            }
        }
        for (char boxOwner : boxPos.keySet()) {
            if (!playerPos.containsKey(boxOwner)) {
                throw new IllegalArgumentException("Boxes for '" + boxOwner + "' but no such player on the map");
            }
        }
        return new GameMap(map, destinations, undoLimit);
    }

    /**
     * Get the entity object at the given position.
     * @param position  the position of the entity in the game map.
     * @return  Entity object.
     * @throws IllegalArgumentException  if position is null or out of map bounds.
     */
    public Entity getEntity(Position position) {
        Objects.requireNonNull(position, "position must not be null");
        int x = position.x(), y = position.y();
        if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight) {
            throw new IllegalArgumentException(
                "Position " + position + " is out of bounds [0," + maxWidth + ")×[0," + maxHeight + ")");
        }
        return map.getOrDefault(position, new Empty());
    }

    /**
     * Put one entity at the given position in the game map.
     * @param position  the position in the game map to put the entity.
     * @param entity    the entity to put into game map.
     * @throws IllegalArgumentException  if position is null or out of map bounds, or if entity is null.
     */
    public void putEntity(@NotNull Position position, @NotNull Entity entity) {
        Objects.requireNonNull(position, "position must not be null");
        Objects.requireNonNull(entity,   "entity must not be null");
        int x = position.x(), y = position.y();
        if (x < 0 || x >= maxWidth || y < 0 || y >= maxHeight) {
            throw new IllegalArgumentException(
                "Position " + position + " is out of bounds [0," + maxWidth + ")×[0," + maxHeight + ")");
        }
        map.put(position, entity);
    }

    /**
     * Get all box destination positions as a set in the game map.
     * @return  a set of positions.
     */
    public Set<Position> getDestinations() {
        return destinations;
    }

    /**
     * Get the undo limit of the game map.
     *
     * @return Optional.empty() when unlimited (-1),
     *         otherwise Optional.of(limit).
     */
    public Optional<Integer> getUndoLimit() {
        return undoLimit == -1
             ? Optional.empty()
             : Optional.of(undoLimit);
    }

    /**
     * Get the maximum width of the game map.
     *
     * @return  maximum width.
     */
    public int getMaxWidth() {
        return maxWidth;
    }
}