
package pa1.model;

import org.jetbrains.annotations.NotNull;
import pa1.util.GameBoardUtils;

import java.util.Objects;

/**
 * A rectangular game board holding cells.
 */
public class GameBoard {

    private final int numRows;
    private final int numCols;
    private final Cell[][] cells;

    /**
     * Constructs a new board with the given dimensions.
     *
     * @param numRows number of rows (must be > 0)
     * @param numCols number of columns (must be > 0)
     * @throws IllegalArgumentException if rows or cols are <= 0.
     */
    public GameBoard(int numRows, int numCols) {
        if (numRows <= 0 || numCols <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive: rows="
                    + numRows + ", cols=" + numCols);
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.cells = new Cell[numRows][numCols];
        // initialize all cells to EmptyCell (or whatever the default is)
        GameBoardUtils.populateWithEmpty(this.cells);
    }

    /**
     * Returns the number of rows in this board.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Returns the number of columns in this board.
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Returns the cell at the given row/col.
     *
     * @throws IllegalArgumentException if out of bounds.
     */
    @NotNull
    public Cell getCell(int row, int col) {
        if (!isInBounds(row, col)) {
            throw new IllegalArgumentException("Position out of bounds: (" + row + "," + col + ")");
        }
        return cells[row][col];
    }

    /**
     * Returns the cell at the given position.
     *
     * @throws IllegalArgumentException if out of bounds.
     */
    @NotNull
    public Cell getCell(@NotNull Position pos) {
        Objects.requireNonNull(pos, "Position must not be null");
        return getCell(pos.row(), pos.col());
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < numRows
            && col >= 0 && col < numCols;
    }

    // … other methods (setCell, clone, equals/hashCode, etc.) …

}