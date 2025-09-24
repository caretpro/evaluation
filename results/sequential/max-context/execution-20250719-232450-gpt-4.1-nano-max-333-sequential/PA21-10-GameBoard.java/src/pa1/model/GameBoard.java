
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @param board   2D array of cells.
     */
    public GameBoard(int numRows, int numCols, @NotNull Cell[][] board) {
        if (board.length != numRows) {
            throw new IllegalArgumentException("cells array length does not match numRows");
        }
        if (board.length > 0 && board[0].length != numCols) {
            throw new IllegalArgumentException("cells array width does not match numCols");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = Objects.requireNonNull(board);
        this.player = getSinglePlayer(); // Initialize or set as needed
    }

    /**
     * Checks that a single player exists on the game board, and returns the instance of the player.
     *
     * @return The single instance of player on the game board.
     * @throws IllegalArgumentException if the game board has zero, or more than one player entities.
     */
    @NotNull
    private Player getSinglePlayer() {
        Player player = null;
        for (final var row : board) {
            for (final var cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    if (player != null) {
                        throw new IllegalArgumentException();
                    }
                    player = p;
                }
            }
        }
        if (player == null) {
            throw new IllegalArgumentException();
        }
        return player;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public Cell getCell(final int row, final int col) {
        if (row < 0 || row >= getNumRows() || col < 0 || col >= getNumCols()) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds");
        }
        return board[row][col];
    }

    public Cell getCell(final Position position) {
        Objects.requireNonNull(position);
        return getCell(position.row(), position.col());
    }

    public Player getPlayer() {
        return this.player;
    }

    // Other methods as needed...
}