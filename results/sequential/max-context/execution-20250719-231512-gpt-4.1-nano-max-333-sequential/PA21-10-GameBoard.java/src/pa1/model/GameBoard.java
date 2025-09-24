
package pa1.model;

import org.jetbrains.annotations.NotNull;

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

    // Existing methods...

    /**
     * Constructor to initialize the game board with given dimensions and cells.
     *
     * @param numRows Number of rows.
     * @param numCols Number of columns.
     * @param cells   2D array of cells.
     */
    public GameBoard(int numRows, int numCols, @NotNull Cell[][] cells) {
        if (cells.length != numRows) {
            throw new IllegalArgumentException("cells.length must be equal to numRows");
        }
        if (cells.length == 0 || cells[0].length != numCols) {
            throw new IllegalArgumentException("cells[0].length must be equal to numCols");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = cells;
        this.player = getSinglePlayer(); // Or assign appropriately if known
    }
}