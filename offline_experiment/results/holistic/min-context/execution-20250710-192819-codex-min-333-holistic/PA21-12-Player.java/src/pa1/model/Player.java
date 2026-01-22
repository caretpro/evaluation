
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The abstract base for entities that can be placed on an {@link EntityCell}.
 *
 * @see Player
 * @see Wall
 */
public abstract class Entity {

    /** The cell this entity belongs to; null if not placed yet. */
    private @Nullable EntityCell owner;

    /**
     * Creates an entity not yet placed on any cell.
     */
    protected Entity() {
        this.owner = null;
    }

    /**
     * Creates an entity and places it on the given cell.
     *
     * @param owner the initial cell; must not be null
     */
    protected Entity(final @NotNull EntityCell owner) {
        this.owner = owner;
    }

    /**
     * Returns the cell this entity belongs to, or null if not placed.
     *
     * @return the current owner cell, or null
     */
    public @Nullable EntityCell getOwner() {
        return owner;
    }

    /**
     * Moves this entity to a new cell (or removes it if null).
     *
     * @param newOwner the new owner cell, or null to remove
     */
    public void setOwner(final @Nullable EntityCell newOwner) {
        this.owner = newOwner;
    }

    /**
     * Unicode‐graphical representation of this entity.
     *
     * @return the Unicode character
     */
    public abstract char toUnicodeChar();

    /**
     * ASCII‐graphical representation of this entity.
     *
     * @return the ASCII character
     */
    public abstract char toASCIIChar();
}