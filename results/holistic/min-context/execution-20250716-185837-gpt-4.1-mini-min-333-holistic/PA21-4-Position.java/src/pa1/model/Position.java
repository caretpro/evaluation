
package pa1.model;

import java.util.Objects;

/**
 * Represents the game board.
 */
public class GameBoard {

    private final int numRows;
    private final int numCols;
    private final Cell[][] cells;
    private Position playerPosition;

    /**
     * Constructs a GameBoard with the given cells.
     *
     * @param cells 2D array of cells representing the board.
     * @throws IllegalArgumentException if cells is null, empty, or malformed,
     *                                  or if player position is invalid or missing.
     */
    public GameBoard(Cell[][] cells) {
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
        for (int r = 0; r < cells.length; r++) {
            if (cells[r] == null || cells[r].length != cols) {
                throw new IllegalArgumentException("All rows must have the same number of columns and not be null.");
            }
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] == null) {
                    throw new IllegalArgumentException("Cell at (" + r + "," + c + ") cannot be null.");
                }
            }
        }
        this.numRows = cells.length;
        this.numCols = cols;
        this.cells = new Cell[numRows][numCols];

        // Copy cells to internal array
        for (int r = 0; r < numRows; r++) {
            System.arraycopy(cells[r], 0, this.cells[r], 0, numCols);
        }

        // Find player position
        Position foundPlayerPos = null;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                if (cells[r][c].hasPlayer()) {
                    if (foundPlayerPos != null) {
                        throw new IllegalArgumentException("Multiple players found on the board.");
                    }
                    foundPlayerPos = new Position(r, c);
                }
            }
        }
        if (foundPlayerPos == null) {
            throw new IllegalArgumentException("No player found on the board.");
        }
        this.playerPosition = foundPlayerPos;
    }

    // Other constructors, methods, and logic...

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Cell getCell(int row, int col) {
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            throw new IllegalArgumentException("Cell position out of bounds.");
        }
        return cells[row][col];
    }

    public Position getPlayerPosition() {
        return playerPosition;
    }

    // Additional methods as needed...
}