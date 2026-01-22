
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
     * Constructor to initialize the game board with specified dimensions and cells.
     *
     * @param numRows Number of rows.
     * @param numCols Number of columns.
     * @param cells   2D array of cells representing the board state.
     */
    public GameBoard(int numRows, int numCols, @NotNull Cell[][] cells) {
        if (cells == null) {
            throw new IllegalArgumentException("Cells array cannot be null");
        }
        if (cells.length != numRows) {
            throw new IllegalArgumentException("cells.length does not match numRows");
        }
        if (cells.length == 0 || cells[0] == null) {
            throw new IllegalArgumentException("Cells array cannot be empty");
        }
        if (cells[0].length != numCols) {
            throw new IllegalArgumentException("cells[0].length does not match numCols");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            if (cells[r] == null || cells[r].length != numCols) {
                throw new IllegalArgumentException("All rows must be non-null and match numCols");
            }
            System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
        }
        Player foundPlayer = null;
        int gemCount = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = this.board[r][c];
                if (cell instanceof EntityCell ec) {
                    if (ec.getEntity() instanceof Player p) {
                        if (foundPlayer != null) {
                            throw new IllegalArgumentException("More than one player found");
                        }
                        foundPlayer = p;
                    }
                    if (ec.getEntity() instanceof Gem) {
                        gemCount++;
                    }
                }
            }
        }
        if (foundPlayer == null) {
            throw new IllegalArgumentException("No player found");
        }
        if (gemCount == 0) {
            throw new IllegalArgumentException("No gems found");
        }
        Position playerPos = Objects.requireNonNull(foundPlayer.getOwner()).getPosition();
        List<Position> reachablePositions = getAllReachablePositions(playerPos);
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = this.board[r][c];
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    Position gemPos = new Position(r, c);
                    if (!reachablePositions.contains(gemPos)) {
                        throw new IllegalArgumentException("A gem is not reachable");
                    }
                }
            }
        }
        this.player = foundPlayer;
    }

    // Existing methods...

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
        Objects.requireNonNull(position, "Position cannot be null");
        int row = position.row();
        int col = position.col();
        if (row < 0 || row >= getNumRows() || col < 0 || col >= getNumCols()) {
            throw new IndexOutOfBoundsException("Position out of bounds");
        }
        return board[row][col];
    }

    /**
     * Additional constructor to create a game board with specified dimensions and cells.
     *
     * @param numRows Number of rows.
     * @param numCols Number of columns.
     * @param cells   2D array of cells representing the board state.
     */
    public GameBoard(int numRows, int numCols, @NotNull Cell[][] cells) {
        this(numRows, numCols, cells);
    }
}