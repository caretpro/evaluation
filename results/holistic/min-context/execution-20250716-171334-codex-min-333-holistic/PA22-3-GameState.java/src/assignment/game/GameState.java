
package assignment.game;

import assignment.entities.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The immutable, original Sokoban game map.
 */
public class GameMap {

    private final int maxWidth;
    private final int maxHeight;
    private final Set<Position> boxDestinationPositions;
    private final Map<Position, Entity> entities;
    private final @Nullable Integer undoLimit;

    public GameMap(
            int maxWidth,
            int maxHeight,
            @NotNull Set<Position> boxDestinationPositions,
            @NotNull Map<Position, Entity> entities,
            @Nullable Integer undoLimit
    ) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.boxDestinationPositions = boxDestinationPositions;
        this.entities = entities;
        this.undoLimit = undoLimit;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    /** 
     * Returns the set of box-destination positions.
     */
    @NotNull
    @Unmodifiable
    public Set<Position> getBoxDestinationPositions() {
        return boxDestinationPositions;
    }

    /**
     * Returns a map from every position to the entity on that position.
     */
    @NotNull
    @Unmodifiable
    public Map<Position, Entity> getEntities() {
        return entities;
    }

    /**
     * Returns the maximum number of undo actions, or empty if unlimited.
     */
    public Optional<Integer> getUndoLimit() {
        return Optional.ofNullable(undoLimit);
    }
}