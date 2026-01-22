
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

    public Entity getEntity(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        if (position.x() < 0 || position.y() < 0 || position.x() >= maxWidth || position.y() >= maxHeight) {
            return new Wall();
        }
        return map.getOrDefault(position, new Empty());
    }

    public void putEntity(Position position, Entity entity) {
        if (position == null || entity == null) {
            throw new IllegalArgumentException("Position and entity cannot be null");
        }
        if (position.x() < 0 || position.y() < 0 || position.x() >= maxWidth || position.y() >= maxHeight) {
            throw new IllegalArgumentException("Position out of bounds");
        }
        map.put(position, entity);
    }

    // ... [rest of the methods remain exactly the same as in the previous version] ...
}