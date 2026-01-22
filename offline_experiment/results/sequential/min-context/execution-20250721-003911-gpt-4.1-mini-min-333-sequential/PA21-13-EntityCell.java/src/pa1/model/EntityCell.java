
package pa1.model;

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
 * <p>
 * More information is provided in the Javadoc of the methods in this class.
 * </p>
 */
public class EntityCell extends Cell {

    /**
     * The entity which resides on this cell.
     */
    @Nullable
    Entity entity = null;

    /**
     * Constructs an EntityCell with the given position and no entity.
     *
     * @param position The position of this cell.
     */
    public EntityCell(final Position position) {
        super(position);
        this.entity = null;
    }

    /**
     * Constructs an EntityCell with the given position and initial entity.
     *
     * @param position The position of this cell.
     * @param initialEntity The initial entity to place on this cell, or null if none.
     */
    public EntityCell(final Position position, @Nullable final Entity initialEntity) {
        super(position);
        if (initialEntity != null) {
            setEntity(initialEntity);
        } else {
            this.entity = null;
        }
    }

    /**
     * @return The entity currently present in this cell, or {@code null} if there is no entity present.
     */
    public final Entity getEntity() {
        return this.entity;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }

    @Override
    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }

    /**
     * Sets the entity for this cell, updating ownership accordingly.
     *
     * @param newEntity The new entity to place on this cell.
     * @return The previous entity that was on this cell, or null if none.
     */
    public Entity setEntity(final Entity newEntity) {
        Entity previousEntity = this.entity;
        if (previousEntity != null) {
            previousEntity.setOwner(null);
        }
        if (newEntity != null) {
            EntityCell previousOwner = newEntity.getOwner();
            if (previousOwner != null) {
                previousOwner.entity = null;
            }
            newEntity.setOwner(this);
        }
        this.entity = newEntity;
        return previousEntity;
    }
}