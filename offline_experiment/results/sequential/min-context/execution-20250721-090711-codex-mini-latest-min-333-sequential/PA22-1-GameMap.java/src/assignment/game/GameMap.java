
package assignment.game;

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A snapshot of the current state of the game.
 */
public class GameState {
    private final GameMap gameMap;
    private final Map<Position, Entity> cellMap;
    private final int undoCount;

    /**
     * Initialize a new game state from a game map.
     *
     * @param gameMap the static game map.
     */
    public GameState(@NotNull GameMap gameMap) {
        this.gameMap = gameMap;
        // copy only the initialized cells—do not loop over empty coordinates
        this.cellMap = new HashMap<>(gameMap.map);  
        this.undoCount = 0;
    }

    /**
     * Copy constructor for undo/redo functionality.
     *
     * @param other existing state to copy.
     */
    private GameState(GameState other) {
        this.gameMap = other.gameMap;
        this.cellMap = new HashMap<>(other.cellMap);
        this.undoCount = other.undoCount + 1;
    }

    /**
     * Get the entity at the given position in this state.
     *
     * @param pos the position.
     * @return the entity.
     */
    public Entity getEntity(Position pos) {
        return Optional.ofNullable(cellMap.get(pos))
                .orElseThrow(() -> new IndexOutOfBoundsException(
                    String.format("Position %s is outside of the map or not initialized", pos)));
    }

    /**
     * Return a new state representing the result of undoing one move.
     *
     * @return the previous state.
     */
    public GameState undo() {
        if (gameMap.getUndoLimit().map(limit -> undoCount < limit).orElse(true)) {
            return new GameState(this);
        } else {
            throw new IllegalStateException("Undo limit reached");
        }
    }

    /**
     * Get all player IDs currently on the map.
     *
     * @return set of player IDs.
     */
    public Set<Integer> getAllPlayerIds() {
        return cellMap.values().stream()
                .filter(e -> e instanceof Player)
                .map(e -> ((Player)e).getId())
                .collect(Collectors.toUnmodifiableSet());
    }
}