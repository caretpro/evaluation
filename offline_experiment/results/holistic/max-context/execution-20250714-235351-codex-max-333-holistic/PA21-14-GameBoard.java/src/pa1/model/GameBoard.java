
package pa1.model;

import org.jetbrains.annotations.NotNull;

/**
 * A cell on the game board.
 */
public abstract class Cell {

    /**
     * @return A fresh copy of this cell.
     */
    @NotNull
    public abstract Cell copy();
}