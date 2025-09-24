
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.IntStream;

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
        if (cells.length != numRows
                || IntStream.range(0, cells.length).anyMatch(r -> cells[r].length != numCols)) {
            throw new IllegalArgumentException("Board dimensions do not match provided cells array.");
        }

        this.numRows = numRows;
        this.numCols = numCols;

        // Deep‐copy cells so that external changes won't affect us
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            System.arraycopy(cells[r], 0, this.board[r], 0, numCols);
        }

        // Find single player and set its owner cell
        this.player = getSinglePlayer();
        this.player.setOwner(getEntityCell(this.player.getOwner().getPosition()));

        // Ensure at least one gem exists
        if (getNumGems() == 0) {
            throw new IllegalArgumentException("There must be at least one Gem on the board.");
        }
        // Ensure all gems are reachable
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Not all gems are reachable from the player's start.");
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
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    if (found != null) {
                        throw new IllegalArgumentException("More than one Player found on the board.");
                    }
                    found = p;
                    // link back the entity‐cell for later use
                    p.setOwner(ec);
                }
            }
        }
        if (found == null) {
            throw new IllegalArgumentException("No Player found on the board.");
        }
        return found;
    }

    /**
     * Retrieves the instance of {@link EntityCell} by a position with an offset.
     *
     * @param pos    Position.
     * @param offset Positional offset to apply.
     * @return An instance of {@link Position}, or {@code null} if the resulting position does not contain an entity cell.
     */
    @Nullable
    private Position getEntityCellByOffset(@NotNull final Position pos, @NotNull final PositionOffset offset) {
        final var newPos = pos.offsetByOrNull(offset, getNumRows(), getNumCols());
        if (newPos == null) {
            return null;
        }
        if (!(getCell(newPos) instanceof EntityCell)) {
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
        final Deque<Position> stack = new ArrayDeque<>();
        stack.push(initialPosition);

        while (!stack.isEmpty()) {
            final var curr = stack.pop();
            if (!(getCell(curr) instanceof EntityCell)
                    || allStoppablePos.contains(curr)) {
                continue;
            }
            allStoppablePos.add(curr);

            for (var dir : Direction.values()) {
                // roll until hit wall or border
                for (int i = 1; i < Math.max(getNumRows(), getNumCols()); i++) {
                    var offset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
                    var pos = curr.offsetByOrNull(offset, getNumRows(), getNumCols());
                    if (pos == null || getCell(pos) instanceof Wall) {
                        // one step back
                        var backOffset = new PositionOffset(dir.getRowOffset() * (i - 1),
                                                             dir.getColOffset() * (i - 1));
                        var backPos = curr.offsetByOrNull(backOffset, getNumRows(), getNumCols());
                        if (backPos != null && !allStoppablePos.contains(backPos)) {
                            stack.push(backPos);
                        }
                        break;
                    }
                    if (getCell(pos) instanceof StopCell || isBorderCell(pos, dir)) {
                        stack.push(pos);
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
        for (var stopPos : getAllStoppablePositions(initialPosition)) {
            for (var dir : Direction.values()) {
                for (int i = 1; i < Math.max(getNumRows(), getNumCols()); i++) {
                    var offset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
                    var pos = stopPos.offsetByOrNull(offset, getNumRows(), getNumCols());
                    if (pos == null || getCell(pos) instanceof Wall) {
                        break;
                    }
                    if (!allReachable.contains(pos)) {
                        allReachable.add(pos);
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
            case UP    -> cellPos.row() == 0;
            case DOWN  -> cellPos.row() == getNumRows() - 1;
            case LEFT  -> cellPos.col() == 0;
            case RIGHT -> cellPos.col() == getNumCols() - 1;
        };
    }

    /**
     * Checks whether all gems are reachable from the player's initial position.
     *
     * @return {@code true} if all gems are reachable.
     */
    private boolean isAllGemsReachable() {
        final int totalGems = getNumGems();
        final Position start = Objects.requireNonNull(player.getOwner()).getPosition();
        final List<Position> reachable = getAllReachablePositions(start);

        long found = reachable.stream()
                .map(this::getCell)
                .filter(c -> c instanceof EntityCell ec && ec.getEntity() instanceof Gem)
                .count();

        return found == totalGems;
    }

    /**
     * Returns the {@link Cell}s of a single row of the game board.
     *
     * @param row Row index.
     * @return 1D array representing the row. The first element in the array corresponds to the leftmost element of the
     * row.
     */
    public Cell[] getRow(final int row) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("Row out of range: " + row);
        }
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
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("Column out of range: " + col);
        }
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
        if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("Cell position out of bounds: (" + row + "," + col + ")");
        }
        return board[row][col];
    }

    /**
     * Returns a single cell of the game board.
     *
     * @param position The position object representing the location of the cell.
     * @return The {@link Cell} instance at the specified location.
     */
    public Cell getCell(final Position position) {
        Objects.requireNonNull(position, "Position cannot be null");
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
            throw new IllegalArgumentException(
                    "Cell at (" + row + "," + col + ") is not an EntityCell: " + c.getClass().getSimpleName());
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
        Objects.requireNonNull(position, "Position cannot be null");
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
        for (var row : board) {
            for (var cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    gems++;
                }
            }
        }
        return gems;
    }
}