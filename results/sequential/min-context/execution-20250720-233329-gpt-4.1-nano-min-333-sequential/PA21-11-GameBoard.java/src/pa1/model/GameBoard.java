
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
     * Constructor to initialize the game board with specified dimensions and cell array.
     *
     * @param numRows Number of rows.
     * @param numCols Number of columns.
     * @param cells   2D array of cells representing the board.
     */
    public GameBoard(int numRows, int numCols, @NotNull Cell[][] cells) {
        if (cells.length != numRows) {
            throw new IllegalArgumentException("Cells array row count does not match numRows");
        }
        if (cells.length == 0 || cells[0].length != numCols) {
            throw new IllegalArgumentException("Cells array column count does not match numCols");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            if (cells[r].length != numCols) {
                throw new IllegalArgumentException("All rows must have the same number of columns");
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
            throw new IllegalArgumentException("No gems in the game board");
        }
        Position playerPos = null;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = this.board[r][c];
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    playerPos = p.getOwner().getPosition();
                    break;
                }
            }
            if (playerPos != null)
                break;
        }
        if (playerPos == null) {
            throw new IllegalStateException("Player position not found");
        }
        List<Position> reachablePositions = getAllReachablePositions(playerPos);
        int reachableGems = 0;
        for (final var pos : reachablePositions) {
            Cell cell = getCell(pos);
            if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                reachableGems++;
            }
        }
        if (reachableGems != gemCount) {
            throw new IllegalArgumentException("Some gems are unreachable");
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
        return getCell(row, col);
    }

    public EntityCell getEntityCell(final int row, final int col) {
        Cell cell = getCell(row, col);
        if (!(cell instanceof EntityCell entityCell)) {
            throw new IllegalArgumentException("Cell at (" + row + ", " + col + ") is not an EntityCell");
        }
        return entityCell;
    }

    public EntityCell getEntityCell(final Position position) {
        final Cell cell = getCell(position);
        if (!(cell instanceof EntityCell entityCell)) {
            throw new IllegalArgumentException("Cell at position " + position + " is not an EntityCell");
        }
        return entityCell;
    }

    // Other existing methods...
}