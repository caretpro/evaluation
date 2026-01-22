
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
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position The position where this cell belongs at.
     */
    public EntityCell(final Position position) {
        super(position);
        this.entity = null;
    }

    /**
     * Creates an instance of {@link EntityCell} at the given game board position with an initial entity.
     *
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity to place on this cell.
     */
    public EntityCell(final Position position, final Entity initialEntity) {
        super(position);
        this.entity = null;
        if (initialEntity != null) {
            setEntity(initialEntity);
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

    public Entity setEntity(final Entity newEntity) {
        final Entity previousEntity = this.entity;
        if (previousEntity != null) {
            previousEntity.setOwner(null);
        }
        if (newEntity != null) {
            final EntityCell previousOwnerCell = newEntity.getOwner();
            if (previousOwnerCell != null && previousOwnerCell != this) {
                previousOwnerCell.entity = null;
            }
            newEntity.setOwner(this);
        }
        this.entity = newEntity;
        return previousEntity;
    }
}