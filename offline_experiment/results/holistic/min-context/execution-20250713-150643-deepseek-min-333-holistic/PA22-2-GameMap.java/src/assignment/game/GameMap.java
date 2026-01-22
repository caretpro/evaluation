
package assignment.game;

import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;

import java.util.*;
import java.util.stream.Collectors;

public class GameMap {
    private final Map<Position, Entity> map;
    private final int maxWidth;
    private final int maxHeight;
    private final Set<Position> destinations;
    private final int undoLimit;

    public GameMap(int maxWidth, int maxHeight, Set<Position> destinations, int undoLimit) {
        if (maxWidth <= 0 || maxHeight <= 0) throw new IllegalArgumentException("Dimensions must be positive");
        if (destinations == null) throw new IllegalArgumentException("Destinations cannot be null");
        if (undoLimit < -1) throw new IllegalArgumentException("Invalid undo limit");
        
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        if (map == null || destinations == null) throw new IllegalArgumentException("Map and destinations cannot be null");
        
        this.map = Collections.unmodifiableMap(new HashMap<>(map));
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream().mapToInt(Position::x).max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream().mapToInt(Position::y).max().orElse(0) + 1;
    }

    public static GameMap parse(String mapText) {
        if (mapText == null || mapText.isEmpty()) throw new IllegalArgumentException("Map text cannot be null or empty");
        
        String[] lines = mapText.split("\n");
        if (lines.length < 2) throw new IllegalArgumentException("Map text must contain at least two lines");

        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("First line must be a valid integer for undo limit");
        }

        if (undoLimit < -1) throw new IllegalArgumentException("Undo limit must be -1 (unlimited) or non-negative");

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();

        for (int y = 1; y < lines.length; y++) {
            if (lines[y] == null) continue;
            
            for (int x = 0; x < lines[y].length(); x++) {
                char c = lines[y].charAt(x);
                Position pos = new Position(x, y - 1);

                switch (c) {
                    case '#': map.put(pos, new Wall()); break;
                    case '@': destinations.add(pos); map.put(pos, new Empty()); break;
                    case '.': map.put(pos, new Empty()); break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int playerId = c - 'A';
                            if (playerIds.contains(playerId)) throw new IllegalArgumentException("Duplicate player: " + c);
                            playerIds.add(playerId);
                            map.put(pos, new Player(playerId));
                        } else if (Character.isLowerCase(c)) {
                            int boxPlayerId = c - 'a';
                            boxPlayerIds.add(boxPlayerId);
                            map.put(pos, new Box(boxPlayerId));
                        } else throw new IllegalArgumentException("Invalid character in map: " + c);
                }
            }
        }

        if (playerIds.isEmpty()) throw new IllegalArgumentException("No players found in the map");
        if (!playerIds.containsAll(boxPlayerIds)) throw new IllegalArgumentException("Some boxes don't have corresponding players");
        if (destinations.size() != boxPlayerIds.size()) throw new IllegalArgumentException("Number of boxes must match number of destinations");

        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        if (position == null) throw new IllegalArgumentException("Position cannot be null");
        return map.getOrDefault(position, new Empty());
    }

    public void putEntity(Position position, Entity entity) {
        if (position == null || entity == null) throw new IllegalArgumentException("Position and entity cannot be null");
        if (position.x() < 0 || position.x() >= maxWidth || position.y() < 0 || position.y() >= maxHeight) {
            throw new IllegalArgumentException("Position out of bounds");
        }
        map.put(position, entity);
    }

    public Set<Position> getDestinations() {
        return destinations;
    }

    public Optional<Integer> getUndoLimit() {
        return undoLimit == -1 ? Optional.empty() : Optional.of(undoLimit);
    }

    public Set<Integer> getPlayerIds() {
        return map.values().stream()
                .filter(Player.class::isInstance)
                .map(e -> ((Player) e).getId())
                .collect(Collectors.toSet());
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }
}