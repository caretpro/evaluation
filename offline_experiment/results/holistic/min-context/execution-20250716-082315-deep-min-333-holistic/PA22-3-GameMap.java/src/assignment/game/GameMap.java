
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
        int undoLimit = Integer.parseInt(lines[0].trim());
        
        if (undoLimit < -1) {
            throw new IllegalArgumentException("Invalid undo limit");
        }

        Map<Position, Entity> entities = new HashMap<>();
        Set<Position> destinations = new HashSet<>();
        Set<Character> players = new HashSet<>();
        Set<Character> boxes = new HashSet<>();

        for (int y = 1; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                Position pos = new Position(x, y - 1);
                
                if (c == '#') {
                    entities.put(pos, new Wall());
                } else if (c == '@') {
                    destinations.add(pos);
                    entities.put(pos, new Empty());
                } else if (Character.isUpperCase(c)) {
                    if (players.contains(c)) {
                        throw new IllegalArgumentException("Duplicate player: " + c);
                    }
                    players.add(c);
                    entities.put(pos, new Player(c - 'A'));
                } else if (Character.isLowerCase(c)) {
                    boxes.add(c);
                    entities.put(pos, new Box(c - 'a'));
                } else if (c == '.') {
                    entities.put(pos, new Empty());
                }
            }
        }

        if (players.isEmpty()) {
            throw new IllegalArgumentException("No players in the map");
        }

        if (destinations.size() < boxes.size()) {
            throw new IllegalArgumentException("Not enough destinations for boxes");
        }

        for (char box : boxes) {
            if (!players.contains(Character.toUpperCase(box))) {
                throw new IllegalArgumentException("No matching player for box: " + box);
            }
        }

        return new GameMap(entities, destinations, undoLimit);
    }

    public Entity getEntity(Position position) {
        return map.getOrDefault(position, new Empty());
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