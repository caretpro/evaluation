
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
     * <p>
     * Note: package‑private so tests in pa1.model can access it directly.
     * </p>
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
    }

    /**
     * Creates an instance of {@link EntityCell} at the given game board position.
     *
     * @param position      The position where this cell belongs at.
     * @param initialEntity The initial entity present in this cell (may be null).
     */
    public EntityCell(final Position position, @Nullable final Entity initialEntity) {
        super(position);
        if (initialEntity != null) {
            setEntity(initialEntity);
        }
    }

    /**
     * Replaces the entity currently present in this cell (may be null).
     *
     * <p>
     * This method performs <em>ownership transfer</em> on the new entity.
     * </p>
     * <p>
     * Specifically, besides just setting {@link #entity}, this method also:
     * </p>
     * <ul>
     *     <li>Unsets the {@link Entity#getOwner()} of the previous entity owned by this instance.</li>
     *     <li>Unsets the {@link #entity} of the previous cell owning the {@code newEntity}.</li>
     *     <li>Sets the {@link Entity#getOwner()} of {@code newEntity} to this instance.</li>
     * </ul>
     *
     * @param newEntity The new {@link Entity} to place in this cell (may be null).
     * @return The previous entity in this cell, or {@code null} if none was present.
     */
    @Nullable
    public Entity setEntity(@Nullable final Entity newEntity) {
        // Detach old entity (if any) from this cell
        Entity old = this.entity;
        if (old != null) {
            old.setOwner(null);
            this.entity = null;
        }

        // If there is a new entity, detach it from its previous owner and attach it here
        if (newEntity != null) {
            EntityCell previousOwner = newEntity.getOwner();
            if (previousOwner != null) {
                previousOwner.entity = null;
            }
            this.entity = newEntity;
            newEntity.setOwner(this);
        }

        return old;
    }

    /**
     * @return The entity currently present in this cell, or {@code null} if there is no entity present.
     */
    public final @Nullable Entity getEntity() {
        return entity;
    }

    @Override
    public char toUnicodeChar() {
        return (entity != null) ? entity.toUnicodeChar() : '.';
    }

    @Override
    public char toASCIIChar() {
        return (entity != null) ? entity.toASCIIChar() : '.';
    }
}