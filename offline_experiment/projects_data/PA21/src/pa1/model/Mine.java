package pa1.model;

import pa1.model.Entity;
import pa1.model.EntityCell;
import pa1.model.Player;

/**
 * A mine entity on a game board.
 *
 * <p>
 * A mine, when touched by the {@link Player} entity, will blow up and deduct a life from the player.
 * </p>
 */
public class Mine extends Entity {

    /**
     * Creates an instance of {@link Mine}, initially not present on any {@link EntityCell}.
     */
    public Mine() {
        // TODO
    }

    /**
     * Creates an instance of {@link Mine}.
     *
     * @param owner The initial {@link EntityCell} the mine belongs to.
     */
    public Mine(final EntityCell owner) {
        // TODO
    }

    @Override
    public char toUnicodeChar() {
        return '\u26A0';
    }

    @Override
    public char toASCIIChar() {
        return 'X';
    }
}
