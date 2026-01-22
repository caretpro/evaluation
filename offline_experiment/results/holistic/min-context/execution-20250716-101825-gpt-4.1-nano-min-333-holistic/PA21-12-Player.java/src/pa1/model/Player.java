
package pa1.model;

import org.jetbrains.annotations.NotNull;

/**
 * The player entity on a game board.
 *
 * <p>
 * There should be at most one player entity on a game board.
 * </p>
 */
public final class Player extends Entity {

    /**
     * Creates an instance of {@link Player}, initially not present on any {@link EntityCell}.
     */
    public Player() {
        super(); // Call the superclass constructor
    }

    /**
     * Creates an instance of {@link Player}.
     *
     * @param owner The initial {@link EntityCell} the player belongs to.
     */
    public Player(final EntityCell owner) {
        super();
        this.setOwner(owner); // Associate the player with the owner cell
    }

    @Override
    public char toUnicodeChar() {
        return '\u25EF'; // Unicode character for a large circle
    }

    @Override
    public char toASCIIChar() {
        return '@'; // ASCII representation
    }
}