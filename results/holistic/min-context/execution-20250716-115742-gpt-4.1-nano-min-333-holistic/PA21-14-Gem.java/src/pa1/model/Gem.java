
package pa1.model;

import org.jetbrains.annotations.NotNull;

/**
 * An gem entity on a game board.
 *
 * <p>
 * A gem is the item which the player needs to collect in the game.
 * </p>
 */
public final class Gem extends Entity {

    /**
     * Creates an instance of {@link Gem}, initially not present on any {@link EntityCell}.
     */
    public Gem() {
        super(); // Call the superclass constructor for no owner
    }

    /**
     * Creates an instance of {@link Gem}.
     *
     * @param owner The initial {@link EntityCell} the gem belongs to.
     */
    public Gem(final EntityCell owner) {
        super(owner); // Call the superclass constructor with owner
    }

    @Override
    public char toUnicodeChar() {
        return '\u25C7'; // Unicode for white diamond shape
    }

    @Override
    public char toASCIIChar() {
        return '*'; // ASCII representation
    }
}