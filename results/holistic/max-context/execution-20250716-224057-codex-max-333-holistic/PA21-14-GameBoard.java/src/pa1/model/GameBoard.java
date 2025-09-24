
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
        if (cells == null || cells.length != numRows || cells[0].length != numCols) {
            throw new IllegalArgumentException("Board dimensions must match cells array");
        }
        this.numRows = numRows;
        this.numCols = numCols;

        // Defensive copy of the board
        this.board = new Cell[numRows][numCols];
        for (int r = 0; r < numRows; r++) {
            if (cells[r] == null || cells[r].length != numCols) {
                throw new IllegalArgumentException("Each row in cells must have length numCols");
            }
            for (int c = 0; c < numCols; c++) {
                this.board[r][c] = Objects.requireNonNull(cells[r][c], "Cell cannot be null");
            }
        }

        // Identify the single player on the board
        this.player = getSinglePlayer();

        // Ensure at least one gem exists
        if (getNumGems() == 0) {
            throw new IllegalArgumentException("There must be at least one gem on the board");
        }

        // Ensure all gems reachable from player's starting position
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Not all gems are reachable from the player");
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
                        throw new IllegalArgumentException("More than one player on board");
                    }
                    found = p;
                }
            }
        }
        if (found == null) {
            throw new IllegalArgumentException("No player on board");
        }
        return found;
    }

    /**
     * Retrieves the instance of {@link Position} by a position with an offset.
     *
     * @param pos    Position.
     * @param offset Positional offset to apply.
     * @return An instance of {@link Position}, or {@code null} if the resulting position does not contain an entity
     * cell or is out of bounds.
     */
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

    /**
     * Gets all {@link Position} of cells which the player can stop on from the cell of the {@code initialPosition}.
     *
     * <p>
     * A cell is "stoppable" iff the player can stop on the cell with any combination of valid moves.
     * </p>
     *
     * @param initialPosition The starting position to get all stoppable cells.
     * @return {@link java.util.List} of all positions stoppable from {@code initialPosition}.
     */
    @NotNull
    private java.util.List<Position> getAllStoppablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        var allStoppablePos = new java.util.ArrayList<Position>();
        var posToTraverse = new java.util.ArrayList<Position>();
        posToTraverse.add(initialPosition);

        while (!posToTraverse.isEmpty()) {
            var nextPos = posToTraverse.remove(posToTraverse.size() - 1);
            if (!(getCell(nextPos) instanceof EntityCell) ||
                allStoppablePos.contains(nextPos)) {
                continue;
            }
            allStoppablePos.add(nextPos);

            for (var dir : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    var posOffset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
                    var posToAdd = getEntityCellByOffset(nextPos, posOffset);
                    if (posToAdd == null) {
                        var maxDist = i - 1;
                        if (maxDist > 0) {
                            var beforeOff = new PositionOffset(dir.getRowOffset() * maxDist,
                                                                dir.getColOffset() * maxDist);
                            var beforePos = getEntityCellByOffset(nextPos, beforeOff);
                            if (beforePos != null && !posToTraverse.contains(beforePos)) {
                                posToTraverse.add(beforePos);
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

        return java.util.Collections.unmodifiableList(allStoppablePos);
    }

    /**
     * Gets all {@link Position} of cells which are reachable from the cell of the {@code initialPosition}.
     *
     * <p>
     * A cell is reachable iff the player can move over the cell with any combination of valid moves.
     * </p>
     *
     * @param initialPosition The starting position to get all reachable cells.
     * @return {@link java.util.List} of all positions reachable from {@code initialPosition}.
     */
    @NotNull
    private java.util.List<Position> getAllReachablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        var allReachablePos = new java.util.ArrayList<Position>();
        var allStoppablePos = getAllStoppablePositions(initialPosition);

        for (var reachablePos : allStoppablePos) {
            for (var dir : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    var posOffset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
                    var posToAdd = getEntityCellByOffset(reachablePos, posOffset);
                    if (posToAdd == null) {
                        break;
                    }
                    if (!allReachablePos.contains(posToAdd)) {
                        allReachablePos.add(posToAdd);
                    }
                }
            }
        }

        return java.util.Collections.unmodifiableList(allReachablePos);
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
        int expectedNumOfGems = getNumGems();
        Position initialPosition = Objects.requireNonNull(player.getOwner()).getPosition();
        var playerReachableCells = getAllReachablePositions(initialPosition);

        int actualNumOfGems = 0;
        for (var pos : playerReachableCells) {
            var cell = getCell(pos);
            if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                actualNumOfGems++;
            }
        }
        return expectedNumOfGems == actualNumOfGems;
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
            throw new IndexOutOfBoundsException("Row index out of bounds");
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
            throw new IndexOutOfBoundsException("Column index out of bounds");
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
            throw new IndexOutOfBoundsException("Cell position out of bounds");
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
        Cell cell = getCell(row, col);
        if (!(cell instanceof EntityCell ec)) {
            throw new IllegalArgumentException("Cell at " + row + "," + col + " is not an EntityCell");
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