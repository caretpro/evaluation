
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
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
        // dimension checks
        if (cells.length != numRows) {
            throw new IllegalArgumentException("numRows does not match cells.length");
        }
        if (cells.length > 0 && cells[0].length != numCols) {
            throw new IllegalArgumentException("numCols does not match cells[0].length");
        }

        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];

        // copy cells in and attach owners for EntityCell
        for (int r = 0; r < numRows; r++) {
            if (cells[r].length != numCols) {
                throw new IllegalArgumentException("ragged row " + r);
            }
            for (int c = 0; c < numCols; c++) {
                Cell src = Objects.requireNonNull(cells[r][c], "cell cannot be null");
                // deep‐copy or use reference? we assume cells are immutable or newly provided
                board[r][c] = src;
            }
        }

        // locate the single player instance
        this.player = getSinglePlayer();

        // ensure at least one gem exists
        if (getNumGems() == 0) {
            throw new IllegalArgumentException("no gems on board");
        }
        // ensure all gems reachable
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("some gems are unreachable");
        }
    }

    /**
     * Checks that a single player exists on the game board, and returns the instance of the player.
     *
     * @return The single instance of player on the game board.
     * @throws IllegalArgumentException if the game board has zero, or more than one player entities.
     */
    @NotNull
    private Player getSinglePlayer() {
        Player found = null;
        for (var row : board) {
            for (var cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player pl) {
                    if (found != null) {
                        throw new IllegalArgumentException("more than one player found");
                    }
                    found = pl;
                }
            }
        }
        if (found == null) {
            throw new IllegalArgumentException("no player found");
        }
        return found;
    }

    /**
     * Retrieves the instance of {@link EntityCell} by a position with an offset.
     *
     * @param pos    Position.
     * @param offset Positional offset to apply.
     * @return An instance of {@link EntityCell}, or {@code null} if the resulting position does not contain an entity
     * cell.
     */
    @Nullable
    private Position getEntityCellByOffset(@NotNull final Position pos, @NotNull final PositionOffset offset) {
        Objects.requireNonNull(pos);
        Objects.requireNonNull(offset);
        final var newPos = pos.offsetByOrNull(offset, getNumRows(), getNumCols());
        if (newPos == null) {
            return null;
        }
        if (getCell(newPos) instanceof Wall) {
            return null;
        }
        return newPos;
    }

    /**
     * Gets all {@link Position} of cells which the player can stop on from the cell of the {@code initialPosition}.
     *
     * <p>
     * A cell is "stoppable" iff the player can stop on the cell with any combination of valid moves.
     * </p>
     *
     * @param initialPosition The starting position to get all stoppable cells.
     * @return {@link List} of all positions stoppable from {@code initialPosition}.
     */
    @NotNull
    private List<Position> getAllStoppablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> allStoppablePos = new ArrayList<>();
        final ArrayDeque<Position> stack = new ArrayDeque<>();
        stack.push(initialPosition);

        while (!stack.isEmpty()) {
            final var current = stack.pop();
            if (!(getCell(current) instanceof EntityCell)) {
                continue;
            }
            if (allStoppablePos.contains(current)) {
                continue;
            }
            allStoppablePos.add(current);

            for (var dir : Direction.values()) {
                for (int dist = 1; dist <= Math.max(getNumRows(), getNumCols()); dist++) {
                    var off = new PositionOffset(dir.getRowOffset() * dist, dir.getColOffset() * dist);
                    var next = getEntityCellByOffset(current, off);
                    if (next == null) {
                        int prevDist = dist - 1;
                        if (prevDist > 0) {
                            off = new PositionOffset(dir.getRowOffset() * prevDist, dir.getColOffset() * prevDist);
                            var prev = getEntityCellByOffset(current, off);
                            if (prev != null && !stack.contains(prev)) {
                                stack.push(prev);
                            }
                        }
                        break;
                    }
                    if (getCell(next) instanceof StopCell || isBorderCell(next, dir)) {
                        stack.push(next);
                    }
                }
            }
        }
        return Collections.unmodifiableList(allStoppablePos);
    }

    /**
     * Gets all {@link Position} of cells which are reachable from the cell of the {@code initialPosition}.
     *
     * <p>
     * A cell is reachable iff the player can move over the cell with any combination of valid moves.
     * </p>
     *
     * @param initialPosition The starting position to get all reachable cells.
     * @return {@link List} of all positions reachable from {@code initialPosition}.
     */
    @NotNull
    private List<Position> getAllReachablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> allReachable = new ArrayList<>();
        final List<Position> stoppable = getAllStoppablePositions(initialPosition);

        for (var pos : stoppable) {
            for (var dir : Direction.values()) {
                for (int dist = 1; dist <= Math.max(getNumRows(), getNumCols()); dist++) {
                    var off = new PositionOffset(dir.getRowOffset() * dist, dir.getColOffset() * dist);
                    var next = getEntityCellByOffset(pos, off);
                    if (next == null) {
                        break;
                    }
                    if (!allReachable.contains(next)) {
                        allReachable.add(next);
                    }
                }
            }
        }
        return Collections.unmodifiableList(allReachable);
    }

    /**
     * Checks whether the given cell is a border cell with reference to the direction.
     *
     * @param cellPos The position of the cell.
     * @param dir     The direction is moving at.
     * @return {@code true} if the cell at the specified position is a border cell, i.e. the last cell in the game board
     * before going out of bounds in the given direction.
     */
    private boolean isBorderCell(@NotNull final Position cellPos, @NotNull final Direction dir) {
        return switch (dir) {
            case UP -> cellPos.row() == 0;
            case DOWN -> cellPos.row() == getNumRows() - 1;
            case LEFT -> cellPos.col() == 0;
            case RIGHT -> cellPos.col() == getNumCols() - 1;
        };
    }

    /**
     * Checks whether all gems are reachable from the player's initial position.
     *
     * @return {@code true} if all gems are reachable.
     */
    private boolean isAllGemsReachable() {
        int expected = getNumGems();
        var startPos = Objects.requireNonNull(player.getOwner()).getPosition();
        var reachable = getAllReachablePositions(startPos);
        int found = 0;
        for (var pos : reachable) {
            var cell = getCell(pos);
            if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                found++;
            }
        }
        return expected == found;
    }

    /**
     * Returns the {@link Cell}s of a single row of the game board.
     *
     * @param row Row index.
     * @return 1D array representing the row. The first element in the array corresponds to the leftmost element of the
     * row.
     */
    public Cell[] getRow(final int row) {
        return board[row].clone();
    }

    /**
     * Returns the {@link Cell}s of a single column of the game board.
     *
     * @param col Column index.
     * @return 1D array representing the column. The first element in the array corresponds to the topmost element of
     * the row.
     */
    public Cell[] getCol(final int col) {
        Cell[] colCells = new Cell[getNumRows()];
        for (int r = 0; r < getNumRows(); r++) {
            colCells[r] = board[r][col];
        }
        return colCells;
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
        Objects.requireNonNull(position);
        return getCell(position.row(), position.col());
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
        Cell c = getCell(row, col);
        if (!(c instanceof EntityCell ec)) {
            throw new IllegalArgumentException("not an EntityCell at " + row + "," + col);
        }
        return ec;
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
        Objects.requireNonNull(position);
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
        int count = 0;
        for (var row : board) {
            for (var cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    count++;
                }
            }
        }
        return count;
    }
}