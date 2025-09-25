package pa1.model;

import pa1.model.Cell;
import pa1.model.BoardElement;
import pa1.model.EntityCell;
import pa1.model.Position;
import pa1.model.Wall;

import java.util.*;

/**
 * A single cell on the game board.
 */
public abstract class Cell implements BoardElement {

    private final Position position;

    /**
     * Creates an instance of {@link Cell} at the given position on the game board.
     *
     * @param position The position where this cell belongs at.
     */
    protected Cell(final Position position) {
        this.position = position;
    }

    /**
     * @return The {@link Position} of this cell on the game board.
     */
    public final Position getPosition() {
        return position;
    }
}
