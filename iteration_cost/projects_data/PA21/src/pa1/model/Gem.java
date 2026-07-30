package pa1.model;

import pa1.model.Entity;
import pa1.model.EntityCell;

/**
 * An gem entity on a game board.
 *
 * <p>
 * A gem is the item which the player needs to collect in the game.
 * </p>
 */
public class Gem extends Entity {

    /**
     * Creates an instance of {@link Gem}, initially not present on any {@link EntityCell}.
     */
    public Gem() {
        // TODO
    }

    /**
     * Creates an instance of {@link Gem}.
     *
     * @param owner The initial {@link EntityCell} the gem belongs to.
     */
    public Gem(final EntityCell owner) {
        // TODO
    }

    @Override
    public char toUnicodeChar() {
        return '\u25C7';
    }

    @Override
    public char toASCIIChar() {
        return '*';
    }
}
