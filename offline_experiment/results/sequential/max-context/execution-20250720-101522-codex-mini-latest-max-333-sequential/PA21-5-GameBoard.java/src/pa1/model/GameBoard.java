
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

    // … all other private utility methods unchanged …

    /**
     * Creates an instance using the provided creation parameters.
     * @param numRows  The number of rows in the game board.
     * @param numCols  The number of columns in the game board.
     * @param cells    The initial values of cells.
     * @throws IllegalArgumentException  if any of the following are true:
     *   <ul>
     *     <li>{@code numRows} is not equal to {@code cells.length}</li>
     *     <li>{@code numCols} is not equal to {@code cells[0].length}</li>
     *     <li>There is no player or more than one player in {@code cells}</li>
     *     <li>There are no gems in {@code cells}</li>
     *     <li>There are some gems which cannot be reached by the player</li>
     *   </ul>
     */
    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (cells.length != numRows || cells[0].length != numCols) {
            throw new IllegalArgumentException("Board dimensions must match the cells array");
        }
        final Cell[][] copy = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            if (cells[r].length != numCols) {
                throw new IllegalArgumentException("All rows must have length " + numCols);
            }
            System.arraycopy(cells[r], 0, copy[r], 0, numCols);
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = copy;
        this.player = getSinglePlayer();
        if (getNumGems() == 0) {
            throw new IllegalArgumentException("There must be at least one gem on the board");
        }
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Not all gems are reachable from the player's start");
        }
    }

    // … rest of GameBoard unchanged …

    /**
     * @return The number of gems still present in the game board.
     */
    public int getNumGems() {
        int count = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    count++;
                }
            }
        }
        return count;
    }

    // … other public methods unchanged …

}