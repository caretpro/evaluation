
package pa1.model;

import org.jetbrains.annotations.Nullable;

/**
 * An entity on the game board.
 */
public abstract class Entity implements BoardElement {

    @Nullable
    private EntityCell owner;

    /**
     * @return The {@link EntityCell} owning this entity, or {@code null} if this entity is not bound to a cell.
     */
    @Nullable
    public final EntityCell getOwner() {
        return owner;
    }

    /**
     * Creates an instance of {@link Entity} with no initial owner.
     */
    protected Entity() {
        this(null);
    }

    /**
     * Creates an instance of {@link Entity}.
     * @param owner The initial {@link EntityCell} the entity resides on.
     */
    protected Entity(final EntityCell owner) {
        this.owner = owner;
        if (owner != null) {
            owner.setEntity(this);
        }
    }

    /**
     * Sets the new owner of this entity. <p>
     * Depending on your implementation, you should not need to call {@link EntityCell#setEntity(Entity)},
     * since this method should only be called from {@link EntityCell#setEntity(Entity)}.
     * </p>
     * @param owner The new {@link EntityCell} owning this entity, or {@code null} if this entity is no longer owned by any cell.
     * @return The previous {@link EntityCell} owning this entity, or {@code null} if this entity was not previously owned by any cell.
     */
    public final EntityCell setOwner(final EntityCell owner) {
        EntityCell previous = this.owner;
        this.owner = owner;
        return previous;
    }
}