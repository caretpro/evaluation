
package assignment.game;

import assignment.entities.*;
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
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(destinations);
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        this.map = Collections.unmodifiableMap(new HashMap<>(map));
        this.destinations = Collections.unmodifiableSet(destinations);
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
        String[] lines = mapText.lines().toArray(String[]::new);
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map text must contain at least undo limit and one map line");
        }
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit");
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit cannot be less than -1");
        }
        Set<Position> destinations = new HashSet<>();
        Map<Position, Entity> entities = new HashMap<>();
        Map<Character, Player> playersByChar = new HashMap<>();
        Map<Character, Box> boxesByChar = new HashMap<>();
        Set<Character> playerChars = new HashSet<>();
        int lineCount = lines.length - 1;
        int maxWidth = 0;
        for (int y = 0; y < lineCount; y++) {
            String line = lines[y + 1];
            maxWidth = Math.max(maxWidth, line.length());
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                Position pos = Position.of(x, y);
                switch (ch) {
                    case '#':
                        entities.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        break;
                    case '.':
                        entities.put(pos, new Empty());
                        break;
                    default:
                        if (Character.isUpperCase(ch)) {
                            if (playerChars.contains(ch)) {
                                throw new IllegalArgumentException("Multiple players with same uppercase letter");
                            }
                            Player player = new Player(ch - 'A' + 1); // Assign IDs starting from 1
                            playersByChar.put(ch, player);
                            entities.put(pos, player);
                            playerChars.add(ch);
                        } else if (Character.isLowerCase(ch)) {
                            char upper = Character.toUpperCase(ch);
                            if (!playersByChar.containsKey(upper)) {
                                throw new IllegalArgumentException("Box with no matching player");
                            }
                            int playerId = playersByChar.get(upper).getId();
                            Box box = new Box(playerId);
                            boxesByChar.put(ch, box);
                            entities.put(pos, box);
                        } else {
                            throw new IllegalArgumentException("Invalid map character: " + ch);
                        }
                        break;
                }
            }
        }
        if (playersByChar.isEmpty()) {
            throw new IllegalArgumentException("No players found");
        }
        long boxCount = boxesByChar.size();
        long destinationCount = destinations.size();
        if (boxCount != destinationCount) {
            throw new IllegalArgumentException("Number of boxes does not match number of destinations");
        }
        // Verify boxes' player IDs match existing players
        for (Box box : boxesByChar.values()) {
            if (!playersByChar.containsKey(Character.toUpperCase('A' + box.getPlayerId() - 1))) {
                throw new IllegalArgumentException("Box's player ID does not match any player");
            }
        }
        return new GameMap(maxWidth, lineCount, destinations, undoLimit).withEntities(entities);
    }

    // Helper method to create a new GameMap with entities
    private GameMap withEntities(Map<Position, Entity> entities) {
        return new GameMap(entities, this.destinations, this.undoLimit);
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
     * @return a new GameMap instance with the updated entity.
     */
    public GameMap putEntity(Position position, Entity entity) {
        Map<Position, Entity> newMap = new HashMap<>(map);
        if (entity instanceof Empty) {
            newMap.remove(position);
        } else {
            newMap.put(position, entity);
        }
        return new GameMap(newMap, this.destinations, this.undoLimit);
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
        return undoLimit >= 0 ? Optional.of(undoLimit) : Optional.empty();
    }

    /**
     * Get all players' id as a set.
     *
     * @return a set of player id.
     */
    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> ((Player) entity).getId())
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