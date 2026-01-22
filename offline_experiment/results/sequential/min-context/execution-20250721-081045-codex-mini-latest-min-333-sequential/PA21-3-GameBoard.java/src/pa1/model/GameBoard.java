
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The main game board of the game.
 *
 * <p>
 * The top-left hand corner of the game board is the "origin" of the board (0, 0).
 * </p>
 */
public final class GameBoard {

    /** Number of rows in the game board. */
    private final int numRows;
    /** Number of columns in the game board. */
    private final int numCols;
    /** 2D array representing each cell in the game board. */
    @NotNull
    private final Cell[][] board;
    /** The instance of {@link Player} on this game board. */
    @NotNull
    private final Player player;

    // … existing private helpers omitted for brevity …

    /**
     * Creates an instance using the provided creation parameters.
     *
     * @param numRows The number of rows in the game board.
     * @param numCols The number of columns in the game board.
     * @param cells   The initial values of cells.
     * @throws IllegalArgumentException if any of the following are true:
     *                                  <ul>
     *                                    <li>{@code numRows} does not match {@code cells.length}</li>
     *                                    <li>{@code numCols} does not match {@code cells[0].length}</li>
     *                                    <li>There is no player or more than one player in {@code cells}</li>
     *                                    <li>There are no gems in {@code cells}</li>
     *                                    <li>There are some gems which cannot be reached by the player</li>
     *                                  </ul>
     */
    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (cells == null || cells.length != numRows || cells[0].length != numCols) {
            throw new IllegalArgumentException("Board dimensions do not match cells array");
        }
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            if (cells[r].length != numCols) {
                throw new IllegalArgumentException("Inconsistent row length in cells array");
            }
            System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
        }
        this.numRows = numRows;
        this.numCols = numCols;

        Player foundPlayer = null;
        int gemCount = 0;
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (cell instanceof EntityCell ec) {
                    if (ec.getEntity() instanceof Player p) {
                        if (foundPlayer != null) {
                            throw new IllegalArgumentException("More than one player on the board");
                        }
                        foundPlayer = p;
                    } else if (ec.getEntity() instanceof Gem) {
                        gemCount++;
                    }
                }
            }
        }
        if (foundPlayer == null) {
            throw new IllegalArgumentException("No player on the board");
        }
        if (gemCount == 0) {
            throw new IllegalArgumentException("Board must contain at least one gem");
        }
        this.player = foundPlayer;
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Not all gems are reachable from player's start");
        }
    }

    // … all other methods unchanged …

    /**
     * @return The number of gems still present in the game board.
     */
    public int getNumGems() {
        int gemCount = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    gemCount++;
                }
            }
        }
        return gemCount;
    }

    // … rest of class unchanged …
}