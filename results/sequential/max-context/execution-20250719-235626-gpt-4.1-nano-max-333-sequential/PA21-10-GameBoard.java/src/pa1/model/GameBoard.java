
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

    private final int numRows;
    private final int numCols;
    @NotNull
    private final Cell[][] board;
    @NotNull
    private final Player player;

    /**
     * Constructor to create a GameBoard with specified dimensions and cell array.
     *
     * @param numRows Number of rows.
     * @param numCols Number of columns.
     * @param board   2D array of cells.
     */
    public GameBoard(int numRows, int numCols, @NotNull Cell[][] board) {
        if (board.length != numRows) {
            throw new IllegalArgumentException("board length does not match numRows");
        }
        if (board.length > 0 && board[0].length != numCols) {
            throw new IllegalArgumentException("board width does not match numCols");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            System.arraycopy(board[r], 0, this.board[r], 0, numCols);
        }
        this.player = getSinglePlayer();
    }

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

    public Player getPlayer() {
        return this.player;
    }

    public Cell getCell(final int row, final int col) {
        if (row < 0 || row >= getNumRows() || col < 0 || col >= getNumCols()) {
            throw new IndexOutOfBoundsException("Row or column index out of bounds");
        }
        return board[row][col];
    }

    public Cell getCell(final Position position) {
        Objects.requireNonNull(position);
        int row = position.row();
        int col = position.col();
        if (row < 0 || row >= getNumRows() || col < 0 || col >= getNumCols()) {
            throw new IndexOutOfBoundsException("Position out of bounds");
        }
        return board[row][col];
    }
}