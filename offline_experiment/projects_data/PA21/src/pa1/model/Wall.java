package pa1.model;

import pa1.model.Cell;
import pa1.model.Position;

/**
 * A wall in the game board.
 */
public class Wall extends Cell {

    /**
     * Creates an instance of {@link Wall} at the given game board position.
     *
     * @param position The position where this cell belongs at.
     */
    public Wall(final Position position) {
        super(position);
    }

    @Override
    public char toUnicodeChar() {
        return '\u2588';
    }

    @Override
    public char toASCIIChar() {
        return 'W';
    }
}
