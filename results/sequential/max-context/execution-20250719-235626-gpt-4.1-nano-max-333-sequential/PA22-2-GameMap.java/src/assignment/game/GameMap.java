
package assignment.game;

import assignment.entities.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Sokoban game board.
 */
public class GameMap {

    private final Map<Position, Entity> map;
    private final int maxWidth;
    private final int maxHeight;
    private final Set<Position> destinations;
    private final int undoLimit;

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
     * Get the maximum height of the game map.
     *
     * @return maximum height.
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    public static GameMap parse(String mapText) {
        if (mapText == null || mapText.isEmpty()) {
            throw new IllegalArgumentException("Map text cannot be null or empty");
        }
        String[] lines = mapText.split("\\r?\\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Map text must contain at least one line for undo limit");
        }
        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit value");
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit cannot be less than -1");
        }
        List<String> mapLines = Arrays.asList(lines).subList(1, lines.length);
        int height = mapLines.size();
        int width = mapLines.stream().mapToInt(String::length).max().orElse(0);
        Set<Position> destinations = new HashSet<>();
        Map<Character, Position> boxesMap = new HashMap<>();
        Set<Character> playerIds = new HashSet<>();
        Map<Position, Entity> entityMap = new HashMap<>();
        int boxCount = 0;

        for (int y = 0; y < height; y++) {
            String line = mapLines.get(y);
            for (int x = 0; x < width; x++) {
                char ch = x < line.length() ? line.charAt(x) : '.';
                Position pos = Position.of(x, y);
                switch (ch) {
                    case '#':
                        entityMap.put(pos, new assignment.entities.Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        entityMap.put(pos, new assignment.entities.Empty());
                        break;
                    case '.':
                        entityMap.put(pos, new assignment.entities.Empty());
                        break;
                    default:
                        if (Character.isUpperCase(ch)) {
                            if (playerIds.contains(ch)) {
                                throw new IllegalArgumentException("Multiple players with same ID: " + ch);
                            }
                            playerIds.add(ch);
                            entityMap.put(pos, new assignment.entities.Player(ch));
                        } else if (Character.isLowerCase(ch)) {
                            boxesMap.put(ch, pos);
                            boxCount++;
                            entityMap.put(pos, new assignment.entities.Box(ch));
                        } else {
                            throw new IllegalArgumentException("Invalid character in map: " + ch);
                        }
                }
            }
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("No players found in the map");
        }
        if (boxesMap.size() != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes does not match number of destinations");
        }
        for (Map.Entry<Character, Position> entry : boxesMap.entrySet()) {
            char boxId = entry.getKey();
            if (!playerIds.contains(Character.toUpperCase(boxId))) {
                throw new IllegalArgumentException("Box with ID " + boxId + " has no matching player");
            }
        }
        for (char playerId : playerIds) {
            char lowerBoxId = Character.toLowerCase(playerId);
            if (!boxesMap.containsKey(lowerBoxId)) {
                throw new IllegalArgumentException("Player " + playerId + " has no corresponding box");
            }
        }
        return new GameMap(width, height, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.get(position);
    }

    public void putEntity(Position position, Entity entity) {
        this.map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                .filter(entity -> entity instanceof assignment.entities.Player)
                .map(entity -> ((assignment.entities.Player) entity).getId())
                .collect(Collectors.toSet());
    }

    public int getMaxWidth() {
        return maxWidth;
    }
}