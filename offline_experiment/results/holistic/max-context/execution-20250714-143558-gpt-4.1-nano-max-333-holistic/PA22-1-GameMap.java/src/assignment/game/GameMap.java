
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
        this.map = new HashMap<>(map);
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
        String[] lines = mapText.split("\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Map text is empty");
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

        List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
        int height = mapLines.size();
        int width = mapLines.stream().mapToInt(String::length).max().orElse(0);

        Map<Position, Entity> entityMap = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Map<Character, Player> playersByChar = new HashMap<>();
        Map<Character, Box> boxesByChar = new HashMap<>();
        Set<Integer> playerIds = new HashSet<>();
        List<Box> boxes = new ArrayList<>();

        int playerIdCounter = 1;

        for (int y = 0; y < height; y++) {
            String line = mapLines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                Position pos = Position.of(x, y);
                switch (ch) {
                    case '#':
                        entityMap.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        entityMap.put(pos, new Empty()); // Keep empty, but mark destination
                        break;
                    case '.':
                        entityMap.put(pos, new Empty());
                        break;
                    default:
                        if (Character.isUpperCase(ch)) {
                            // Player
                            if (playersByChar.containsKey(ch)) {
                                throw new IllegalArgumentException("Multiple players with same label: " + ch);
                            }
                            Player player = new Player(playerIdCounter);
                            playersByChar.put(ch, player);
                            entityMap.put(pos, player);
                            playerIds.add(playerIdCounter);
                            playerIdCounter++;
                        } else if (Character.isLowerCase(ch)) {
                            // Box
                            char upperCh = Character.toUpperCase(ch);
                            Player associatedPlayer = playersByChar.get(upperCh);
                            if (associatedPlayer == null) {
                                throw new IllegalArgumentException("Box at " + pos + " has no matching player for character: " + ch);
                            }
                            Box box = new Box(associatedPlayer.getId());
                            boxes.add(box);
                            entityMap.put(pos, box);
                            boxesByChar.put(ch, box);
                        } else {
                            // For other characters, treat as empty
                            entityMap.put(pos, new Empty());
                        }
                        break;
                }
            }
        }

        // Validate players
        if (playersByChar.isEmpty()) {
            throw new IllegalArgumentException("No players found in the map");
        }

        // Count boxes
        long boxCount = boxes.size();
        long destinationCount = destinations.size();
        if (boxCount != destinationCount) {
            throw new IllegalArgumentException("Number of boxes does not match number of destinations");
        }

        // Validate boxes' player IDs
        for (Box box : boxes) {
            int pid = box.getPlayerId();
            if (!playerIds.contains(pid)) {
                throw new IllegalArgumentException("Box's player ID does not match any player");
            }
        }

        return new GameMap(entityMap, destinations, undoLimit);
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
        newMap.put(position, entity);
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
        if (undoLimit == -1) {
            return Optional.of(-1);
        } else if (undoLimit >= 0) {
            return Optional.of(undoLimit);
        } else {
            return Optional.empty();
        }
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