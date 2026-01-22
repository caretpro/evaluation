
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The main game board of the game.
 *
 * <p>
 * The top-left hand corner of the game board is the "origin" of the board (0, 0).
 * </p>
 */
public final class GameBoard {

    /**
     * Number of rows in the game board.
     */
    private final int numRows;
    /**
     * Number of columns in the game board.
     */
    private final int numCols;

    /**
     * 2D array representing each cell in the game board.
     */
    @NotNull
    private final Cell[][] board;

    /**
     * The instance of {@link Player} on this game board.
     */
    @NotNull
    private final Player player;

    /**
     * Constructor to initialize the game board with given dimensions and cell array.
     *
     * @param numRows Number of rows.
     * @param numCols Number of columns.
     * @param cells   2D array of cells.
     */
    public GameBoard(int numRows, int numCols, @NotNull Cell[][] cells) {
        if (cells.length != numRows) {
            throw new IllegalArgumentException("cells.length does not match numRows");
        }
        if (cells.length > 0 && cells[0].length != numCols) {
            throw new IllegalArgumentException("cells[0].length does not match numCols");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
        }
        this.player = getSinglePlayer();
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Some gems are not reachable from the player's position");
        }
    }

    // Existing methods...
    // (getNumGems, getRow, getCol, getCell, getEntityCell, getNumRows, getNumCols, getPlayer, etc.)
}