
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
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
        for (Cell[] row : cells) {
            if (row.length != numCols) {
                throw new IllegalArgumentException("numCols does not match cells[0].length");
            }
        }

        this.numRows = numRows;
        this.numCols = numCols;
        // defensive copy
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
        }

        // find exactly one player
        this.player = getSinglePlayer();

        // check at least one gem exists
        if (getNumGems() == 0) {
            throw new IllegalArgumentException("There must be at least one gem");
        }
        // check all gems are reachable
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Not all gems are reachable");
        }
    }

    @NotNull
    private Player getSinglePlayer() {
        Player found = null;
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    if (found != null) {
                        throw new IllegalArgumentException("More than one player");
                    }
                    found = p;
                }
            }
        }
        if (found == null) {
            throw new IllegalArgumentException("No player found");
        }
        return found;
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
        final Deque<Position> stack = new ArrayDeque<>();
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

            for (Direction dir : Direction.values()) {
                for (int d = 1; d < Math.max(getNumRows(), getNumCols()); d++) {
                    var offset = new PositionOffset(dir.getRowOffset() * d, dir.getColOffset() * d);
                    var next = getEntityCellByOffset(current, offset);
                    if (next == null) {
                        // can't go further; back up one
                        int backDist = d - 1;
                        if (backDist > 0) {
                            var backOffset = new PositionOffset(dir.getRowOffset() * backDist,
                                                                 dir.getColOffset() * backDist);
                            var before = getEntityCellByOffset(current, backOffset);
                            if (before != null && !stack.contains(before)) {
                                stack.push(before);
                            }
                        }
                        break;
                    }
                    // can stop if hitting StopCell or border
                    if (getCell(next) instanceof StopCell || isBorderCell(next, dir)) {
                        stack.push(next);
                    }
                }
            }
        }
        return Collections.unmodifiableList(allStoppablePos);
    }

    @NotNull
    private List<Position> getAllReachablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> reachable = new ArrayList<>();
        final List<Position> stoppable = getAllStoppablePositions(initialPosition);

        for (Position pos : stoppable) {
            for (Direction dir : Direction.values()) {
                for (int d = 1; d < Math.max(getNumRows(), getNumCols()); d++) {
                    var offset = new PositionOffset(dir.getRowOffset() * d, dir.getColOffset() * d);
                    var next = getEntityCellByOffset(pos, offset);
                    if (next == null) {
                        break;
                    }
                    if (!reachable.contains(next)) {
                        reachable.add(next);
                    }
                }
            }
        }
        return Collections.unmodifiableList(reachable);
    }

    private boolean isBorderCell(@NotNull final Position cellPos, @NotNull final Direction dir) {
        return switch (dir) {
            case UP    -> cellPos.row() == 0;
            case DOWN  -> cellPos.row() == getNumRows() - 1;
            case LEFT  -> cellPos.col() == 0;
            case RIGHT -> cellPos.col() == getNumCols() - 1;
        };
    }

    private boolean isAllGemsReachable() {
        int expected = getNumGems();
        Position start = Objects.requireNonNull(getPlayer().getOwner()).getPosition();
        List<Position> reachable = getAllReachablePositions(start);

        int found = 0;
        for (Position pos : reachable) {
            Cell c = getCell(pos);
            if (c instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                found++;
            }
        }
        return found == expected;
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
        Cell[] column = new Cell[numRows];
        for (int r = 0; r < numRows; r++) {
            column[r] = board[r][col];
        }
        return column;
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
            throw new IllegalArgumentException("Not an EntityCell at (" + row + "," + col + ")");
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
        int gems = 0;
        for (Cell[] row : board) {
            for (Cell c : row) {
                if (c instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    gems++;
                }
            }
        }
        return gems;
    }
}