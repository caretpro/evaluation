
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

    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit cannot be less than -1");
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
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    public static GameMap parse(String mapText) {
        String[] lines = mapText.lines().toArray(String[]::new);
        if (lines.length == 0) {
            throw new IllegalArgumentException("Map text cannot be empty");
        }

        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be an integer representing undo limit");
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
        Map<Character, List<Position>> boxesByChar = new HashMap<>();
        Set<Character> playerChars = new HashSet<>();
        Set<Character> boxChars = new HashSet<>();

        // First pass: identify walls, destinations, players, boxes
        for (int y = 0; y < height; y++) {
            String line = mapLines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char ch = line.charAt(x);
                Position pos = new Position(x, y);
                switch (ch) {
                    case '#':
                        entityMap.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        entityMap.put(pos, new Empty()); // placeholder
                        break;
                    case '.':
                        entityMap.put(pos, new Empty());
                        break;
                    default:
                        if (Character.isUpperCase(ch)) {
                            // Player
                            if (!playersByChar.containsKey(ch)) {
                                Player player = new Player(ch - 'A' + 1);
                                playersByChar.put(ch, player);
                                playerChars.add(ch);
                            }
                            entityMap.put(pos, playersByChar.get(ch));
                        } else if (Character.isLowerCase(ch)) {
                            // Box
                            boxChars.add(ch);
                            boxesByChar.computeIfAbsent(ch, k -> new ArrayList<>()).add(pos);
                            entityMap.put(pos, new Box(ch - 'a' + 1));
                        } else {
                            // Empty or unrecognized
                            entityMap.put(pos, new Empty());
                        }
                        break;
                }
            }
        }

        // Validate total boxes count matches destinations
        int totalBoxes = boxesByChar.values().stream().mapToInt(List::size).sum();
        if (totalBoxes != destinations.size()) {
            throw new IllegalArgumentException("Number of boxes (" + totalBoxes + ") does not match number of destinations (" + destinations.size() + ")");
        }

        // Validate boxes' player IDs
        for (Map.Entry<Character, List<Position>> entry : boxesByChar.entrySet()) {
            for (Position pos : entry.getValue()) {
                Entity entity = entityMap.get(pos);
                if (!(entity instanceof Box)) {
                    throw new IllegalArgumentException("Expected Box at " + pos);
                }
                Box box = (Box) entity;
                int playerId = box.getPlayerId();
                boolean match = false;
                for (Player p : playersByChar.values()) {
                    if (p.getId() == playerId) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    throw new IllegalArgumentException("Box at " + pos + " with playerId " + playerId + " does not match any player");
                }
            }
        }

        if (playersByChar.isEmpty()) {
            throw new IllegalArgumentException("No players found in the map");
        }

        return new GameMap(entityMap, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.getOrDefault(position, new Empty());
    }

    public void putEntity(Position position, Entity entity) {
        // Since map is immutable, create a new map with the updated entity
        Map<Position, Entity> newMap = new HashMap<>(map);
        newMap.put(position, entity);
        // To maintain immutability, create a new GameMap instance
        throw new UnsupportedOperationException("GameMap is immutable; create a new instance to modify");
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return undoLimit >= 0 ? Optional.of(undoLimit) : Optional.empty();
    }

    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> ((Player) entity).getId())
                .collect(Collectors.toSet());
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}