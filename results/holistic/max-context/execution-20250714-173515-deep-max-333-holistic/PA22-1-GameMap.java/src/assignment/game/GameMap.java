
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

    public static GameMap parse(String mapText) {
        String[] lines = mapText.split("\n");
        if (lines.length < 2) {
            throw new IllegalArgumentException("Map text must contain at least 2 lines");
        }

        int undoLimit = Integer.parseInt(lines[0].trim());
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Undo limit must be -1 or non-negative");
        }

        Map<Position, Entity> entities = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Character> boxPlayers = new HashSet<>();

        int y = 0;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y);
                
                if (c == '#') {
                    entities.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                    entities.put(pos, new Empty());
                } else if (Character.isUpperCase(c)) {
                    int playerId = c - 'A';
                    if (playerIds.contains(playerId)) {
                        throw new IllegalArgumentException("Duplicate player: " + c);
                    }
                    playerIds.add(playerId);
                    entities.put(pos, new Player(playerId));
                } else if (Character.isLowerCase(c)) {
                    boxPlayers.add(c);
                    entities.put(pos, new Box(c - 'a'));
                } else if (c == '.') {
                    entities.put(pos, new Empty());
                }
            }
            y++;
        }

        if (playerIds.isEmpty()) {
            throw new IllegalArgumentException("No players in the map");
        }

        for (Character boxPlayer : boxPlayers) {
            if (!playerIds.contains(boxPlayer - 'a')) {
                throw new IllegalArgumentException("Box with no corresponding player: " + boxPlayer);
            }
        }

        if (boxPlayers.size() > destinations.size()) {
            throw new IllegalArgumentException("Number of boxes exceeds number of destinations");
        }

        return new GameMap(entities, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.getOrDefault(position, new Empty());
    }

    public void putEntity(Position position, Entity entity) {
        if (entity == null) {
            map.remove(position);
        } else {
            map.put(position, entity);
        }
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