
package pa1.model;

import org.jetbrains.annotations.Nullable;

/**
 * An entity on the game board.
 */
public abstract class Entity implements BoardElement {

    @Nullable
    private EntityCell owner;

    /**
     * Default constructor initializing owner to null.
     */
    public Entity() {
        this.owner = null;
    }

    /**
     * Constructor initializing owner with the provided EntityCell.
     * @param owner the EntityCell to set as owner.
     */
    public Entity(final EntityCell owner) {
        this.owner = owner;
    }

    /**
     * @return The {@link EntityCell} owning this entity, or {@code null} if this entity is not bound to a cell.
     */
    public final EntityCell getOwner() {
        return this.owner;
    }

    protected void Entity() {
        this.owner = null;
    }

    protected void Entity(final EntityCell owner) {
        this.owner = owner;
    }

    public final EntityCell setOwner(final EntityCell owner) {
        EntityCell previousOwner = this.owner;
        this.owner = owner;
        return previousOwner;
    }
}