
package assignment.game;

import assignment.actions.Move;
import assignment.entities.Box;
import assignment.entities.Empty;
import assignment.entities.Entity;
import assignment.entities.Player;
import assignment.entities.Wall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
        if (lines.length < 2) throw new IllegalArgumentException("Map text must contain at least two lines");

        int undoLimit = Integer.parseInt(lines[0].trim());
        if (undoLimit < -1) throw new IllegalArgumentException("Undo limit must be -1 (unlimited) or non-negative");

        Map<Position, Entity> map = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Integer> playerIds = new HashSet<>();
        Set<Integer> boxPlayerIds = new HashSet<>();

        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
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
                        } else {
                            throw new IllegalArgumentException("Invalid character in map: " + c);
                        }
                }
            }
        }

        if (playerIds.isEmpty()) throw new IllegalArgumentException("No players in the map");
        for (int boxPlayerId : boxPlayerIds) {
            if (!playerIds.contains(boxPlayerId)) throw new IllegalArgumentException("Box has no corresponding player");
        }
        for (int playerId : playerIds) {
            if (!boxPlayerIds.contains(playerId)) throw new IllegalArgumentException("Player has no corresponding boxes");
        }
        long boxCount = map.values().stream().filter(e -> e instanceof Box).count();
        if (boxCount != destinations.size()) throw new IllegalArgumentException("Boxes don't match destinations");

        return new GameMap(map, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        Entity entity = map.get(position);
        return entity != null ? entity : new Empty();
    }

    public void putEntity(Position position, Entity entity) {
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
                .filter(e -> e instanceof Player)
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