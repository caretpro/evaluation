
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
     * Creates an instance of {@link Entity}, initially not present on any {@link EntityCell}.
     */
    protected Entity() {
        this(null);
    }

    /**
     * Creates an instance of {@link Entity}.
     *
     * @param owner The initial {@link EntityCell} the entity resides on.
     */
    protected Entity(final EntityCell owner) {
        setOwner(owner);
    }

    public final EntityCell setOwner(final EntityCell owner) {
        EntityCell previous = this.owner;
        this.owner = owner;
        return previous;
    }
}