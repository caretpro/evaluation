
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Cell} on the game board which can contain an {@link Entity}.
 *
 * <p>
 * This class should observe and enforce the following constraints:
 * </p>
 * <ul>
 *     <li>There must be at most one entity owned by each cell; In other words, there should never be two entities'
 *     {@link Entity#getOwner()} pointing to the same {@link EntityCell}.</li>
 *     <li>There must be at most one cell owning each entity; In other words, there should never be two cells'
 *     {@link EntityCell#getEntity()} pointing to the same {@link Entity}.</li>
 * </ul>
 */
public class EntityCell extends Cell {

    /**
     * The entity which resides on this cell.
     */
    @Nullable
    Entity entity = null;

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position The position where this cell belongs at.
     */
    public EntityCell(@NotNull final Position position) {
        super(position);
    }

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity present in this cell.
     */
    public EntityCell(
            @NotNull final Position position,
            @Nullable final Entity initialEntity
    ) {
        this(position);
        setEntity(initialEntity);
    }

    /**
     * Replaces the entity currently present in this cell.
     *
     * @param newEntity The new {@link Entity} to place in this cell.
     * @return The previous entity in this cell, or {@code null} if no entity was previously present.
     */
    @Nullable
    public Entity setEntity(@Nullable final Entity newEntity) {
        final Entity previousEntity = entity;

        if (previousEntity == newEntity) {
            return previousEntity;
        }

        if (previousEntity != null) {
            previousEntity.setOwner(null);
        }

        if (newEntity != null) {
            final EntityCell previousOwner = newEntity.getOwner();

            if (previousOwner != null && previousOwner != this) {
                previousOwner.setEntity(null);
            }

            entity = newEntity;
            newEntity.setOwner(this);
        } else {
            entity = null;
        }

        return previousEntity;
    }

    /**
     * @return The entity currently present in this cell, or {@code null} if there is no entity present.
     */
    @Nullable
    public final Entity getEntity() {
        return entity;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }
}