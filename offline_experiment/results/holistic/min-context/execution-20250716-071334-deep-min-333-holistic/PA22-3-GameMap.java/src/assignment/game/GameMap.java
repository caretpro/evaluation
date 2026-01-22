
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
        if (maxWidth <= 0 || maxHeight <= 0) {
            throw new IllegalArgumentException("Map dimensions must be positive");
        }
        if (destinations == null) {
            throw new IllegalArgumentException("Destinations cannot be null");
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Invalid undo limit");
        }

        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.map = new HashMap<>();
    }

    private GameMap(Map<Position, Entity> map, Set<Position> destinations, int undoLimit) {
        if (map == null || destinations == null) {
            throw new IllegalArgumentException("Map and destinations cannot be null");
        }
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Invalid undo limit");
        }

        this.map = Collections.unmodifiableMap(new HashMap<>(map));
        this.destinations = Collections.unmodifiableSet(new HashSet<>(destinations));
        this.undoLimit = undoLimit;
        this.maxWidth = map.keySet().stream()
                .filter(Objects::nonNull)
                .mapToInt(Position::x)
                .max().orElse(0) + 1;
        this.maxHeight = map.keySet().stream()
                .filter(Objects::nonNull)
                .mapToInt(Position::y)
                .max().orElse(0) + 1;
    }

    public static GameMap parse(String mapText) {
        if (mapText == null || mapText.isEmpty()) {
            throw new IllegalArgumentException("Map text cannot be null or empty");
        }

        String[] lines = mapText.split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Invalid map format");
        }

        int undoLimit;
        try {
            undoLimit = Integer.parseInt(lines[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid undo limit format");
        }

        if (undoLimit < -1) {
            throw new IllegalArgumentException("Invalid undo limit");
        }

        Map<Position, Entity> entities = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();

        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - 1);

                switch (c) {
                    case '#':
                        entities.put(pos, new Wall());
                        break;
                    case '@':
                        destinations.add(pos);
                        entities.put(pos, new Empty());
                        break;
                    case '.':
                        entities.put(pos, new Empty());
                        break;
                    default:
                        if (Character.isUpperCase(c)) {
                            int playerId = c - 'A';
                            if (playerIds.contains(playerId)) {
                                throw new IllegalArgumentException("Duplicate player: " + c);
                            }
                            playerIds.add(playerId);
                            entities.put(pos, new Player(playerId));
                        } else if (Character.isLowerCase(c)) {
                            int boxPlayerId = c - 'a';
                            boxPlayerIds.add(boxPlayerId);
                            entities.put(pos, new Box(boxPlayerId));
                        }
                        break;
                }
            }
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("No players in map");
        }

        if (!playerIds.containsAll(boxPlayerIds)) {
            throw new IllegalArgumentException("Boxes without matching players");
        }

        if (destinations.size() != boxPlayerIds.size()) {
            throw new IllegalArgumentException("Box count doesn't match destination count");
        }

        return new GameMap(entities, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        return map.getOrDefault(position, new Empty());
    }

    public void putEntity(Position position, Entity entity) {
        if (position == null || entity == null) {
            throw new IllegalArgumentException("Position and entity cannot be null");
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
                .filter(Objects::nonNull)
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