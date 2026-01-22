
package pa1.model;

import java.util.Objects;

/**
 * Represents the game board state.
 */
public class GameBoard {

    private final int numRows;
    private final int numCols;
    private final Cell[][] cells;
    private final int numLives; // negative means unlimited lives
    private final Position playerPosition;

    /**
     * Constructs a GameBoard with given parameters.
     *
     * @param numRows       Number of rows on the board, must be positive.
     * @param numCols       Number of columns on the board, must be positive.
     * @param cells         2D array of cells representing the board, must not be null and match dimensions.
     * @param numLives      Number of lives. Negative value indicates unlimited lives.
     * @param playerPosition Initial player position, must be within bounds.
     * @throws IllegalArgumentException if parameters are invalid.
     */
    public GameBoard(int numRows, int numCols, Cell[][] cells, int numLives, Position playerPosition) {
        if (numRows <= 0) {
            throw new IllegalArgumentException("Number of rows must be positive.");
        }
        if (numCols <= 0) {
            throw new IllegalArgumentException("Number of columns must be positive.");
        }
        if (cells == null) {
            throw new IllegalArgumentException("Cells array cannot be null.");
        }
        if (cells.length != numRows) {
            throw new IllegalArgumentException("Cells array row count does not match numRows.");
        }
        for (int r = 0; r < numRows; r++) {
            if (cells[r] == null || cells[r].length != numCols) {
                throw new IllegalArgumentException("Cells array column count does not match numCols at row " + r);
            }
        }
        if (playerPosition == null) {
            throw new IllegalArgumentException("Player position cannot be null.");
        }
        if (playerPosition.row() < 0 || playerPosition.row() >= numRows ||
            playerPosition.col() < 0 || playerPosition.col() >= numCols) {
            throw new IllegalArgumentException("Player position out of board bounds.");
        }
        // Accept negative numLives as unlimited lives, so no exception here.

        this.numRows = numRows;
        this.numCols = numCols;
        this.cells = cells;
        this.numLives = numLives;
        this.playerPosition = playerPosition;
    }

    // Other methods omitted for brevity...

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

    public int getNumLives() {
        return numLives;
    }

    // equals, hashCode, toString as needed...

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameBoard)) return false;
        GameBoard that = (GameBoard) o;
        return numRows == that.numRows &&
               numCols == that.numCols &&
               numLives == that.numLives &&
               Objects.equals(playerPosition, that.playerPosition) &&
               java.util.Arrays.deepEquals(cells, that.cells);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(numRows, numCols, numLives, playerPosition);
        result = 31 * result + java.util.Arrays.deepHashCode(cells);
        return result;
    }
}