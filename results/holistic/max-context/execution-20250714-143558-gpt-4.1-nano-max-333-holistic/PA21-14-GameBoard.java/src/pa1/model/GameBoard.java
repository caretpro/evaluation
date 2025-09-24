
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    @Nullable
    private Player player; // Changed to nullable for deserialization

    /**
     * Constructor for creating a new GameBoard with initial data.
     */
    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (cells == null) {
            throw new IllegalArgumentException("Cells array cannot be null");
        }
        if (cells.length != numRows) {
            throw new IllegalArgumentException("Number of rows does not match cells.length");
        }
        if (numRows > 0 && cells[0].length != numCols) {
            throw new IllegalArgumentException("Number of columns does not match cells[0].length");
        }

        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];

        int gemCount = 0;
        Player tempPlayer = null;

        for (int r = 0; r < numRows; r++) {
            if (cells[r] == null || cells[r].length != numCols) {
                throw new IllegalArgumentException("All rows must be non-null and match the specified number of columns");
            }
            for (int c = 0; c < numCols; c++) {
                Cell cell = cells[r][c];
                if (cell == null) {
                    throw new IllegalArgumentException("Cell at (" + r + "," + c + ") is null");
                }
                this.board[r][c] = cell;

                if (cell instanceof EntityCell ec) {
                    if (ec.getEntity() instanceof Player p) {
                        if (tempPlayer != null) {
                            throw new IllegalArgumentException("More than one player found");
                        }
                        tempPlayer = p;
                    }
                    if (ec.getEntity() instanceof Gem) {
                        gemCount++;
                    }
                }
            }
        }

        if (tempPlayer == null) {
            throw new IllegalArgumentException("No player found");
        }
        if (gemCount == 0) {
            throw new IllegalArgumentException("No gems found");
        }

        // Verify that all gems are reachable from the player's position
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Some gems are unreachable");
        }

        this.player = tempPlayer; // Assign the found player to the field
    }

    // Package-private setter for deserialization
    void setPlayer(@Nullable Player player) {
        this.player = player;
    }

    /**
     * Checks that a single player exists on the game board, and returns the instance of the player.
     *
     * @return The single instance of player on the game board.
     * @throws IllegalArgumentException if the game board has zero, or more than one player entities.
     */
    @NotNull
    public Player getPlayer() {
        if (player == null) {
            throw new IllegalStateException("Player has not been initialized");
        }
        return player;
    }

    /**
     * Checks whether all gems are reachable from the player's initial position.
     *
     * @return {@code true} if all gems are reachable.
     */
    public boolean isAllGemsReachable() {
        if (player == null) {
            // During deserialization, player might be null; handle accordingly
            return false;
        }
        final var initialPosition = Objects.requireNonNull(player.getOwner()).getPosition();
        final var reachablePositions = getAllReachablePositions(initialPosition);
        final var gemPositions = new HashSet<Position>();
        for (int r = 0; r < getNumRows(); r++) {
            for (int c = 0; c < getNumCols(); c++) {
                Cell cell = getCell(r, c);
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    gemPositions.add(new Position(r, c));
                }
            }
        }
        return reachablePositions.containsAll(gemPositions);
    }

    // Rest of the class remains unchanged...

    @NotNull
    public Cell[] getRow(final int row) {
        if (row < 0 || row >= getNumRows()) {
            throw new IndexOutOfBoundsException("Row index out of bounds");
        }
        return Arrays.copyOf(board[row], getNumCols());
    }

    @NotNull
    public Cell[] getCol(final int col) {
        if (col < 0 || col >= getNumCols()) {
            throw new IndexOutOfBoundsException("Column index out of bounds");
        }
        Cell[] column = new Cell[getNumRows()];
        for (int r = 0; r < getNumRows(); r++) {
            column[r] = board[r][col];
        }
        return column;
    }

    public Cell getCell(final int row, final int col) {
        if (row < 0 || row >= getNumRows() || col < 0 || col >= getNumCols()) {
            throw new IndexOutOfBoundsException("Cell position out of bounds");
        }
        return board[row][col];
    }

    public Cell getCell(final Position position) {
        Objects.requireNonNull(position);
        return getCell(position.row(), position.col());
    }

    public EntityCell getEntityCell(final int row, final int col) {
        Cell cell = getCell(row, col);
        if (!(cell instanceof EntityCell ec)) {
            throw new IllegalArgumentException("Cell at (" + row + "," + col + ") is not an EntityCell");
        }
        return ec;
    }

    public EntityCell getEntityCell(final Position position) {
        Cell cell = getCell(position);
        if (!(cell instanceof EntityCell ec)) {
            throw new IllegalArgumentException("Cell at position is not an EntityCell");
        }
        return ec;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }
}