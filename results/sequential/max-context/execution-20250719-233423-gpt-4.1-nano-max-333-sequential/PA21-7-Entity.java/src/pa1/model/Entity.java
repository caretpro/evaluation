
package pa1.model;

import org.jetbrains.annotations.Nullable;

/**
 * An entity on the game board.
 */
public abstract class Entity implements BoardElement {

    @Nullable
    private EntityCell owner;

    /**
     * Default constructor.
     */
    public Entity() {
        this.owner = null;
    }

    /**
     * Constructor that sets the owner of this entity.
     *
     * @param owner The EntityCell that owns this entity.
     */
    public Entity(@Nullable EntityCell owner) {
        this.owner = owner;
    }

    /**
     * @return The {@link EntityCell} owning this entity, or {@code null} if this entity is not bound to a cell.
     */
    public final EntityCell getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this entity.
     *
     * @param owner The new owner EntityCell, or null to unbind.
     */
    public void setOwner(@Nullable EntityCell owner) {
        this.owner = owner;
    }
}