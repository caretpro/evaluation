
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Cell} on the game board which can contain an {@link Entity}.
 *
 * <p>
 * This class maintains exclusive ownership between entities and cells:
 * each cell owns at most one entity, and each entity is owned by at most one
 * cell.
 * </p>
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
     * Replaces the entity currently present in this cell and transfers ownership
     * to the new entity.
     *
     * @param newEntity The new entity to place in this cell, or {@code null}.
     * @return The previous entity in this cell, or {@code null} if none was present.
     */
    @Nullable
    public Entity setEntity(@Nullable final Entity newEntity) {
        final Entity previousEntity = this.entity;

        if (previousEntity == newEntity) {
            if (newEntity != null && newEntity.getOwner() != this) {
                final EntityCell previousOwner = newEntity.getOwner();
                if (previousOwner != null) {
                    previousOwner.setEntity(null);
                }
                newEntity.setOwner(this);
            }
            return previousEntity;
        }

        if (previousEntity != null && previousEntity.getOwner() == this) {
            previousEntity.setOwner(null);
        }

        if (newEntity != null) {
            final EntityCell previousOwner = newEntity.getOwner();

            if (previousOwner != null && previousOwner != this) {
                previousOwner.setEntity(null);
            }
        }

        this.entity = newEntity;

        if (newEntity != null) {
            newEntity.setOwner(this);
        }

        return previousEntity;
    }

    /**
     * @return The entity currently present in this cell, or {@code null} if no
     * entity is present.
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