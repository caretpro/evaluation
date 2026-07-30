
package pa1.model;

import org.jetbrains.annotations.Nullable;

/**
 * An entity on the game board.
 */
public abstract class Entity implements BoardElement {

    @Nullable
    private EntityCell owner;

    /**
     * Creates an entity that is not initially present on any cell.
     */
    protected Entity() {
        this(null);
    }

    /**
     * Creates an entity initially residing on the given cell.
     *
     * @param owner the initial owning cell, or {@code null}
     */
    protected Entity(@Nullable final EntityCell owner) {
        this.owner = null;

        if (owner != null) {
            owner.setEntity(this);
        }
    }

    /**
     * Updates the cell owning this entity.
     *
     * @param owner the new owning cell, or {@code null}
     * @return the previous owning cell, or {@code null}
     */
    public final EntityCell setOwner(@Nullable final EntityCell owner) {
        final EntityCell previousOwner = this.owner;
        this.owner = owner;
        return previousOwner;
    }

    /**
     * Returns the cell owning this entity.
     *
     * @return the owning cell, or {@code null}
     */
    @Nullable
    public final EntityCell getOwner() {
        return owner;
    }
}