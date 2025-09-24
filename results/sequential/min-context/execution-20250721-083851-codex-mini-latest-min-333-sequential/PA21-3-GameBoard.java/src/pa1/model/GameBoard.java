
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The main game board of the game.
 *
 * <p>
 * The top-left hand corner of the game board is the "origin" of the board (0, 0).
 * </p>
 */
public final class GameBoard {

    // … other fields and methods omitted for brevity …

    /**
     * Constructs a game board of the given dimensions and cells.
     *
     * @param numRows Number of rows in the game board.
     * @param numCols Number of columns in the game board.
     * @param cells   2D array of {@link Cell} instances matching those dimensions.
     * @throws IllegalArgumentException if dimensions mismatch, zero/multiple players, zero gems, or unreachable gems.
     */
    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (cells.length != numRows || cells[0].length != numCols) {
            throw new IllegalArgumentException("numRows/numCols must match cells dimensions");
        }
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            if (cells[r].length != numCols) {
                throw new IllegalArgumentException("ragged array");
            }
            System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.player = getSinglePlayer();
        if (getNumGems() == 0) {
            throw new IllegalArgumentException("there must be at least one gem");
        }
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("some gems are unreachable");
        }
    }

    // … rest of class …
}