
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
public class EntityCell extends Cell implements BoardElement {

    /**
     * The entity which resides on this cell.
     */
    @Nullable
    private Entity entity = null;

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position The position where this cell belongs at.
     */
    public EntityCell(final Position position) {
        super(position);
    }

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity present in this cell.
     */
    public EntityCell(final Position position, final Entity initialEntity) {
        super(position);
        setEntity(initialEntity);
    }

    /**
     * Replaces the entity currently present in this cell.
     *
     * <p>
     * This method should perform <em>ownership transfer</em> on the new entity.
     * </p>
     * <p>
     * In addition to setting {@link EntityCell#entity}, this method should:
     * </p>
     * <ul>
     *     <li>Unset the {@link Entity#getOwner()} of the previous entity owned by this instance.</li>
     *     <li>Unset the {@link EntityCell#entity} of the previous cell owning the {@code newEntity}.</li>
     *     <li>Set the {@link Entity#getOwner()} of {@code newEntity} to this instance.</li>
     * </ul>
     *
     * @param newEntity The new {@link Entity} to place in this cell.
     * @return The previous entity in this cell, or {@code null} if no entity was previously present.
     */
    public Entity setEntity(final Entity newEntity) {
        Entity previousEntity = this.entity;

        if (previousEntity != null) {
            previousEntity.setOwner(null);
        }

        if (newEntity != null) {
            Entity currentOwner = newEntity.getOwner();
            if (currentOwner != null) {
                currentOwner.setOwner(null);
            }
            newEntity.setOwner(this);
        }

        this.entity = newEntity;
        return previousEntity;
    }

    /**
     * @return The entity currently present in this cell, or {@code null} if there is no entity present.
     */
    @Override
    public final Entity getEntity() {
        return this.entity;
    }

    @Override
    public char toUnicodeChar() {
        return getEntity() != null ? getEntity().toUnicodeChar() : '.';
    }

    public char toASCIIChar() {
        return getEntity() != null ? getEntity().toASCIIChar() : '.';
    }
}