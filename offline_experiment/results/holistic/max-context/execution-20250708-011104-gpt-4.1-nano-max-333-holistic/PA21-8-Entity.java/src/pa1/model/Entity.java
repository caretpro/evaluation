
package pa1.model;

import org.jetbrains.annotations.Nullable;

/**
 * An entity on the game board.
 */
public abstract class Entity implements BoardElement {

    @Nullable
    private EntityCell owner;

    /**
     * Creates an instance of {@link Entity}, initially not present on any {@link EntityCell}.
     */
    protected Entity() {
        this.owner = null; // Initially not owned by any cell
    }

    /**
     * Creates an instance of {@link Entity}.
     *
     * @param owner The initial {@link EntityCell} the entity resides on.
     */
    protected Entity(final EntityCell owner) {
        this.owner = null; // Initialize owner to null
        // Set the owner cell if provided
        if (owner != null) {
            owner.setEntity(this);
        }
    }

    /**
     * Sets the new owner of this entity.
     *
     * <p>
     * Depending on your implementation, you should not need to call {@link EntityCell#setEntity(Entity)}, since this
     * method should only be called from {@link EntityCell#setEntity(Entity)}.
     * </p>
     *
     * @param owner The new {@link EntityCell} owning this entity, or {@code null} if this entity is no longer owned by
     *              any cell.
     * @return The previous {@link EntityCell} owning this entity, or {@code null} if this entity was not previously
     * owned by any cell.
     */
    public final EntityCell setOwner(final EntityCell owner) {
        EntityCell previousOwner = this.owner;
        this.owner = owner;
        return previousOwner;
    }

    /**
     * @return The {@link EntityCell} owning this entity, or {@code null} if this entity is not bound to a cell.
     */
    public final EntityCell getOwner() {
        return owner;
    }
}