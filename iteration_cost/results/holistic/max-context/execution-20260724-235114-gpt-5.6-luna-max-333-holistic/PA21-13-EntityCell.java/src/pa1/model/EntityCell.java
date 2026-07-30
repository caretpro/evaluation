
package pa1.model;

import org.jetbrains.annotations.Nullable;

/**
 * A {@link Cell} on the game board which can contain an {@link Entity}.
 *
 * <p>
 * This class enforces one-to-one ownership between entities and entity cells.
 * </p>
 */
public class EntityCell extends Cell {

    /**
     * The entity which resides on this cell.
     */
    @Nullable
    Entity entity = null;

    /**
     * Creates an empty entity cell at the given game board position.
     *
     * @param position The position where this cell belongs.
     */
    public EntityCell(final Position position) {
        super(position);
    }

    /**
     * Creates an entity cell at the given game board position.
     *
     * @param position      The position where this cell belongs.
     * @param initialEntity The initial entity present in this cell.
     */
    public EntityCell(final Position position, final Entity initialEntity) {
        this(position);
        setEntity(initialEntity);
    }

    /**
     * Replaces the entity currently present in this cell and transfers ownership
     * from any previous cell owning the new entity.
     *
     * @param newEntity The new entity to place in this cell, or {@code null}.
     * @return The previous entity in this cell, or {@code null} if none was present.
     */
    @Nullable
    public Entity setEntity(@Nullable final Entity newEntity) {
        final Entity previousEntity = this.entity;

        if (previousEntity == newEntity) {
            return previousEntity;
        }

        if (previousEntity != null) {
            previousEntity.setOwner(null);
        }

        if (newEntity != null) {
            final EntityCell previousOwner = newEntity.getOwner();

            if (previousOwner != null && previousOwner != this) {
                previousOwner.entity = null;
            }

            newEntity.setOwner(this);
        }

        this.entity = newEntity;
        return previousEntity;
    }

    /**
     * @return The entity currently present in this cell, or {@code null} if empty.
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