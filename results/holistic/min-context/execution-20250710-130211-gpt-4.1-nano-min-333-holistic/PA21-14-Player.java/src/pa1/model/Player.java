
package pa1.model;

/**
 * Abstract base class for entities on the game board.
 */
public abstract class Entity {

    private EntityCell owner;

    /**
     * Creates an Entity with no owner.
     */
    public Entity() {
        this.owner = null;
    }

    /**
     * Creates an Entity with an initial owner.
     *
     * @param owner The EntityCell that owns this entity.
     */
    public Entity(final EntityCell owner) {
        this.owner = owner;
    }

    /**
     * Gets the owner of this entity.
     *
     * @return The EntityCell owner, or null if none.
     */
    public EntityCell getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this entity.
     *
     * @param owner The EntityCell to set as owner.
     */
    public void setOwner(EntityCell owner) {
        this.owner = owner;
    }

    /**
     * Returns the Unicode character representing this entity.
     *
     * @return Unicode character.
     */
    public abstract char toUnicodeChar();

    /**
     * Returns the ASCII character representing this entity.
     *
     * @return ASCII character.
     */
    public abstract char toASCIIChar();
}