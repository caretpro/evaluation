
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
     * Creates an instance using the provided creation parameters.
     *
     * @param numRows The number of rows in the game board.
     * @param numCols The number of columns in the game board.
     * @param cells   The initial values of cells.
     * @throws IllegalArgumentException if any of the following are true:
     *                                  <ul>
     *                                      <li>{@code numRows} is not equal to {@code cells.length}</li>
     *                                      <li>{@code numCols} is not equal to {@code cells[0].length}</li>
     *                                      <li>There is no player or more than one player in {@code cells}</li>
     *                                      <li>There are no gems in {@code cells}</li>
     *                                      <li>There are some gems which cannot be reached by the player</li>
     *                                  </ul>
     */
    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        // Validate dimensions
        if (numRows != cells.length || numCols != cells[0].length) {
            throw new IllegalArgumentException("numRows/numCols mismatch with cells dimensions");
        }

        this.numRows = numRows;
        this.numCols = numCols;

        // Defensive copy of the board
        this.board = new Cell[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            this.board[i] = cells[i].clone(); // shallow copy: cells are assumed immutable or handled elsewhere
        }

        // Validate player count (exactly one)
        this.player = getSinglePlayer();

        // Validate at least one gem
        int gemCount = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = this.board[r][c];
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    gemCount++;
                }
            }
        }
        if (gemCount == 0) {
            throw new IllegalArgumentException("Board must contain at least one gem");
        }

        // Validate all gems are reachable from the player's initial position
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Not all gems are reachable by the player");
        }
    }

    // ---- Private helper methods (already provided) ----

    @NotNull
    private Player getSinglePlayer() {
        Player player = null;
        for (final var row : board) {
            for (final var cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    if (player != null) {
                        throw new IllegalArgumentException("More than one player found on the board");
                    }
                    player = p;
                }
            }
        }
        if (player == null) {
            throw new IllegalArgumentException("No player found on the board");
        }
        return player;
    }

    @Nullable
    private Position getEntityCellByOffset(@NotNull final Position pos, @NotNull final PositionOffset offset) {
        final var newPos = pos.offsetByOrNull(offset, getNumRows(), getNumCols());
        if (newPos == null) {
            return null;
        }
        if (getCell(newPos) instanceof Wall) {
            return null;
        }
        return newPos;
    }

    @NotNull
    private List<Position> getAllStoppablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> allStoppablePos = new ArrayList<>();
        final List<Position> posToTraverse = new ArrayList<>();

        posToTraverse.add(initialPosition);

        while (!posToTraverse.isEmpty()) {
            final var nextPos = posToTraverse.remove(posToTraverse.size() - 1);

            if (!(getCell(nextPos) instanceof EntityCell)) {
                continue;
            }
            if (allStoppablePos.contains(nextPos)) {
                continue;
            }
            allStoppablePos.add(nextPos);

            for (@NotNull final var dir : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    final var posOffset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
                    final var posToAdd = getEntityCellByOffset(nextPos, posOffset);

                    if (posToAdd == null) {
                        final var maxDist = i - 1;
                        if (maxDist > 0) {
                            final var posOffsetBeforeThis = new PositionOffset(dir.getRowOffset() * maxDist,
                                    dir.getColOffset() * maxDist);
                            final var posBeforeThis = getEntityCellByOffset(nextPos, posOffsetBeforeThis);

                            if (posBeforeThis != null && !posToTraverse.contains(posBeforeThis)) {
                                posToTraverse.add(posBeforeThis);
                            }
                        }
                        break;
                    }

                    if (getCell(posToAdd) instanceof StopCell || isBorderCell(posToAdd, dir)) {
                        posToTraverse.add(posToAdd);
                    }
                }
            }
        }

        return Collections.unmodifiableList(allStoppablePos);
    }

    @NotNull
    private List<Position> getAllReachablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> allReachablePos = new ArrayList<>();
        final List<Position> allStoppablePos = getAllStoppablePositions(initialPosition);

        for (@NotNull final var reachablePos : allStoppablePos) {
            for (@NotNull final var dir : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    final var posOffset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
                    final var posToAdd = getEntityCellByOffset(reachablePos, posOffset);
                    if (posToAdd == null) {
                        break;
                    }

                    if (!allReachablePos.contains(posToAdd)) {
                        allReachablePos.add(posToAdd);
                    }
                }
            }
        }

        return Collections.unmodifiableList(allReachablePos);
    }

    private boolean isBorderCell(@NotNull final Position cellPos, @NotNull final Direction dir) {
        return switch (dir) {
            case UP -> cellPos.row() == 0;
            case DOWN -> cellPos.row() == getNumRows() - 1;
            case LEFT -> cellPos.col() == 0;
            case RIGHT -> cellPos.col() == getNumCols() - 1;
        };
    }

    private boolean isAllGemsReachable() {
        final var expectedNumOfGems = getNumGems();

        final var initialPosition = Objects.requireNonNull(getPlayer().getOwner()).getPosition();
        final var playerReachableCells = getAllReachablePositions(initialPosition);

        int actualNumOfGems = 0;
        for (final var pos : playerReachableCells) {
            final var cell = getCell(pos);
            if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                ++actualNumOfGems;
            }
        }

        return expectedNumOfGems == actualNumOfGems;
    }

    // ---- Public methods (to be implemented) ----

    /**
     * Returns the {@link Cell}s of a single row of the game board.
     *
     * @param row Row index.
     * @return 1D array representing the row. The first element in the array corresponds to the leftmost element of the
     *         row.
     */
    public Cell[] getRow(final int row) {
        return board[row].clone(); // Return a copy to protect internal state
    }

    /**
     * Returns the {@link Cell}s of a single column of the game board.
     *
     * @param col Column index.
     * @return 1D array representing the column. The first element in the array corresponds to the topmost element of
     *         the row.
     */
    public Cell[] getCol(final int col) {
        Cell[] colArray = new Cell[numRows];
        for (int i = 0; i < numRows; i++) {
            colArray[i] = board[i][col];
        }
        return colArray;
    }

    /**
     * Returns a single cell of the game board.
     *
     * @param row Row index.
     * @param col Column index.
     * @return The {@link Cell} instance at the specified location.
     */
    public Cell getCell(final int row, final int col) {
        return board[row][col];
    }

    /**
     * Returns a single cell of the game board.
     *
     * @param position The position object representing the location of the cell.
     * @return The {@link Cell} instance at the specified location.
     */
    public Cell getCell(final Position position) {
        return board[position.row()][position.col()];
    }

    /**
     * Returns an {@link EntityCell} on the game board.
     *
     * <p>
     * This method is a convenience method for getting a cell which is unconditionally known to be an entity cell.
     * </p>
     *
     * @param row Row index.
     * @param col Column index.
     * @return The {@link EntityCell} instance at the specified location.
     * @throws IllegalArgumentException if the cell at the specified position is not an instance of {@link EntityCell}.
     */
    public EntityCell getEntityCell(final int row, final int col) {
        Cell cell = getCell(row, col);
        if (!(cell instanceof EntityCell)) {
            throw new IllegalArgumentException("Cell at (" + row + "," + col + ") is not an EntityCell");
        }
        return (EntityCell) cell;
    }

    /**
     * Returns an {@link EntityCell} on the game board.
     *
     * <p>
     * This method is a convenience method for getting a cell which is unconditionally known to be an entity cell.
     * </p>
     *
     * @param position The position object representing the location of the cell.
     * @return The {@link EntityCell} instance at the specified location.
     * @throws IllegalArgumentException if the cell at the specified position is not an instance of {@link EntityCell}.
     */
    public EntityCell getEntityCell(final Position position) {
        return getEntityCell(position.row(), position.col());
    }

    /**
     * @return The number of rows of this game board.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * @return The number of columns of this game board.
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * @return The player instance.
     */
    public Player getPlayer() {
        return player;
    }

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
}