
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;                  // ← add this
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

    // … all your other private helpers unchanged …

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

    /**
     * Creates an instance using the provided creation parameters.
     * @param numRows  The number of rows in the game board.
     * @param numCols  The number of columns in the game board.
     * @param cells    The initial values of cells.
     * @throws IllegalArgumentException  if any of the following are true: <ul> <li> {@code  numRows}  is not equal to  {@code  cells.length} </li> <li> {@code  numCols}  is not equal to  {@code  cells[0].length} </li> <li>There is no player or more than one player entities.</li> <li>There are no gems in  {@code  cells}</li> <li>There are some gems which cannot be reached by the player</li> </ul>
     */
    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (cells.length != numRows || cells[0].length != numCols) {
            throw new IllegalArgumentException("Board dimensions mismatch");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            if (cells[r].length != numCols) {
                throw new IllegalArgumentException("Board row length mismatch at row " + r);
            }
            for (int c = 0; c < numCols; c++) {
                this.board[r][c] = Objects.requireNonNull(cells[r][c], "Cell cannot be null");
            }
        }
        this.player = getSinglePlayer();
        if (getNumGems() <= 0) {
            throw new IllegalArgumentException("No gems on board");
        }
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Some gems are unreachable");
        }
    }

    /**
     * Returns the {@link Cell}s of a single row of the game board.
     * @param row  Row index.
     * @return  1D array representing the row. The first element in the array corresponds to the leftmost element of the row.
     * @throws IndexOutOfBoundsException  if {@code row} is not in [0, numRows).
     */
    public Cell[] getRow(final int row) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("Row index out of range: " + row);
        }
        return Arrays.copyOf(board[row], numCols);
    }

    /**
     * Returns the {@link Cell}s of a single column of the game board.
     * @param col  Column index.
     * @return  1D array representing the column. The first element in the array corresponds to the topmost element of the column.
     * @throws IndexOutOfBoundsException  if {@code col} is not in [0, numCols).
     */
    public Cell[] getCol(final int col) {
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("Column index out of range: " + col);
        }
        final Cell[] column = new Cell[numRows];
        for (int r = 0; r < numRows; r++) {
            column[r] = board[r][col];
        }
        return column;
    }

    /**
     * Returns a single cell of the game board.
     * @param row  Row index.
     * @param col  Column index.
     * @return  The {@link Cell} instance at the specified location.
     */
    public Cell getCell(final int row, final int col) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("Row index out of range: " + row);
        }
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("Column index out of range: " + col);
        }
        return board[row][col];
    }

    /**
     * Returns a single cell of the game board.
     * @param position  The position object representing the location of the cell.
     * @return  The {@link Cell} instance at the specified location.
     */
    public Cell getCell(final Position position) {
        Objects.requireNonNull(position, "Position cannot be null");
        return getCell(position.row(), position.col());
    }

    // --- NOTE: remove @Override here because no supertypes declare this exact signature ---
    public EntityCell getEntityCell(final int row, final int col) {
        final Cell cell = getCell(row, col);
        if (!(cell instanceof EntityCell)) {
            throw new IllegalArgumentException("Cell at (" + row + ", " + col + ") is not an EntityCell");
        }
        return (EntityCell) cell;
    }

    /**
     * Returns an {@link EntityCell} on the game board.
     * This method is a convenience method for getting a cell which is unconditionally known to be an entity cell.
     * @param position  The position object representing the location of the cell.
     * @return  The {@link EntityCell} instance at the specified location.
     * @throws IllegalArgumentException  if the cell at the specified position is not an instance of {@link EntityCell}.
     */
    public EntityCell getEntityCell(final Position position) {
        Objects.requireNonNull(position, "Position cannot be null");
        final Cell cell = getCell(position);
        if (!(cell instanceof EntityCell)) {
            throw new IllegalArgumentException(
                String.format("Cell at (%d, %d) is not an EntityCell", position.row(), position.col()));
        }
        return (EntityCell) cell;
    }

    /**
     * @return  The number of rows of this game board.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * @return  The number of columns of this game board.
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * @return  The player instance.
     */
    public Player getPlayer() {
        return player;
    }

    // … rest of your methods unchanged …

    // private helpers like getSinglePlayer(), isAllGemsReachable(), etc.
}