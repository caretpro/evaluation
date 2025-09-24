
package pa1.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents the game board.
 */
public class GameBoard {

    private final int numRows;
    private final int numCols;
    private final Cell[][] cells;
    private final int numLives; // negative means unlimited lives

    /**
     * Constructs a GameBoard with the given cells and number of lives.
     *
     * @param cells    2D array of Cells representing the board.
     * @param numLives Number of lives; negative means unlimited.
     * @throws IllegalArgumentException if cells is null, empty, or irregular.
     */
    public GameBoard(Cell[][] cells, int numLives) {
        if (cells == null) {
            throw new IllegalArgumentException("Cells array cannot be null.");
        }
        if (cells.length == 0) {
            throw new IllegalArgumentException("Cells array must have at least one row.");
        }
        int cols = cells[0].length;
        if (cols == 0) {
            throw new IllegalArgumentException("Cells array must have at least one column.");
        }
        for (int i = 1; i < cells.length; i++) {
            if (cells[i] == null || cells[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same number of columns and not be null.");
            }
        }
        this.numRows = cells.length;
        this.numCols = cols;
        this.cells = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            System.arraycopy(cells[r], 0, this.cells[r], 0, numCols);
        }
        this.numLives = numLives;
    }

    /**
     * Constructs a GameBoard with the given cells and unlimited lives.
     *
     * @param cells 2D array of Cells representing the board.
     * @throws IllegalArgumentException if cells is null or empty or irregular.
     */
    public GameBoard(Cell[][] cells) {
        this(cells, -1); // unlimited lives by default
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumLives() {
        return numLives;
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            throw new IllegalArgumentException("Row or column out of bounds.");
        }
        return cells[row][col];
    }

    // Other methods...

    // Cell class or interface should be defined elsewhere in the package.
}