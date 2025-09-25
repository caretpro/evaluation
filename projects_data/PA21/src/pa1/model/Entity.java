package pa1.model;

import pa1.model.BoardElement;
import pa1.model.EntityCell;
import pa1.model.ExtraLife;
import pa1.model.Gem;
import pa1.model.Mine;
import pa1.model.Player;

/**
 * An entity on the game board.
 */
public abstract class Entity implements BoardElement {

    private EntityCell owner;

    /**
     * Creates an instance of {@link Entity}, initially not present on any {@link EntityCell}.
     */
    protected Entity() {
        // TODO
    }

    /**
     * Creates an instance of {@link Entity}.
     *
     * @param owner The initial {@link EntityCell} the entity resides on.
     */
    protected Entity(final EntityCell owner) {
        // TODO
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
        // TODO
        return null;
    }

    /**
     * @return The {@link EntityCell} owning this entity, or {@code null} if this entity is not bound to a cell.
     */
    public final EntityCell getOwner() {
        // TODO
        return null;
    }
}
