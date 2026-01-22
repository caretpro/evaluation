
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
        if (cells == null) {
            throw new IllegalArgumentException("Cells array cannot be null");
        }
        if (cells.length != numRows) {
            throw new IllegalArgumentException("Number of rows does not match cells array length");
        }
        if (cells.length > 0 && cells[0].length != numCols) {
            throw new IllegalArgumentException("Number of columns does not match cells array width");
        }
        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];

        // Copy cells to internal array
        for (int r = 0; r < numRows; r++) {
            if (cells[r] == null) {
                throw new IllegalArgumentException("Row " + r + " in cells array is null");
            }
            if (cells[r].length != numCols) {
                throw new IllegalArgumentException("Row " + r + " length does not match numCols");
            }
            for (int c = 0; c < numCols; c++) {
                if (cells[r][c] == null) {
                    throw new IllegalArgumentException("Cell at (" + r + "," + c + ") is null");
                }
                this.board[r][c] = cells[r][c];
            }
        }

        // Find the player
        Player foundPlayer = null;
        int gemCount = 0;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    if (foundPlayer != null) {
                        throw new IllegalArgumentException("More than one player found");
                    }
                    foundPlayer = p;
                }
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    gemCount++;
                }
            }
        }

        if (foundPlayer == null) {
            throw new IllegalArgumentException("No player found");
        }

        if (gemCount == 0) {
            throw new IllegalArgumentException("No gems in the game board");
        }

        // Check reachability of all gems
        Position playerPos = null;
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                Cell cell = board[r][c];
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    playerPos = new Position(r, c);
                    break;
                }
            }
            if (playerPos != null) break;
        }
        if (playerPos == null) {
            throw new IllegalArgumentException("Player position not found");
        }

        List<Position> reachablePositions = getAllReachablePositions(playerPos);
        int reachableGems = 0;
        for (Position pos : reachablePositions) {
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

    /**
     * Checks that a single player exists on the game board, and returns the instance of the player.
     *
     * @return The single instance of player on the game board.
     * @throws IllegalArgumentException if the game board has zero, or more than one player entities.
     */
    @NotNull
    private Player getSinglePlayer() {
        Player player = null;
        for (final var row : board) {
            for (final var cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
                    if (player != null) {
                        throw new IllegalArgumentException("Multiple players found");
                    }
                    player = p;
                }
            }
        }
        if (player == null) {
            throw new IllegalArgumentException("No player found");
        }
        return player;
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
        final Set<Position> visited = new HashSet<>();
        final Deque<Position> toVisit = new ArrayDeque<>();
        toVisit.add(initialPosition);

        while (!toVisit.isEmpty()) {
            final var currentPos = toVisit.removeFirst();
            if (visited.contains(currentPos)) continue;
            visited.add(currentPos);

            Cell currentCell = getCell(currentPos);
            if (!(currentCell instanceof EntityCell)) {
                continue;
            }
            if (allStoppablePos.contains(currentPos)) {
                continue;
            }
            allStoppablePos.add(currentPos);

            for (@NotNull final var dir : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    final var posOffset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
                    final var posToAdd = getEntityCellByOffset(currentPos, posOffset);

                    if (posToAdd == null) {
                        final var maxDist = i - 1;
                        if (maxDist > 0) {
                            final var posOffsetBeforeThis = new PositionOffset(dir.getRowOffset() * maxDist,
                                    dir.getColOffset() * maxDist);
                            final var posBeforeThis = getEntityCellByOffset(currentPos, posOffsetBeforeThis);
                            if (posBeforeThis != null && !visited.contains(posBeforeThis)) {
                                toVisit.addLast(posBeforeThis);
                            }
                        }
                        break;
                    }

                    if (getCell(posToAdd) instanceof StopCell || isBorderCell(posToAdd, dir)) {
                        if (!visited.contains(posToAdd)) {
                            toVisit.addLast(posToAdd);
                        }
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

        final Set<Position> reachablePositions = new HashSet<>();
        final Queue<Position> toVisit = new ArrayDeque<>();
        toVisit.add(initialPosition);
        reachablePositions.add(initialPosition);

        while (!toVisit.isEmpty()) {
            final var currentPos = toVisit.poll();

            for (@NotNull final var dir : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    final var posOffset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
                    final var posToAdd = getEntityCellByOffset(currentPos, posOffset);
                    if (posToAdd == null) {
                        break;
                    }
                    if (!reachablePositions.contains(posToAdd)) {
                        reachablePositions.add(posToAdd);
                        toVisit.add(posToAdd);
                    }
                }
            }
        }

        return new ArrayList<>(reachablePositions);
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
        return Arrays.copyOf(board[row], numCols);
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
        int r = position.row();
        int c = position.col();
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            throw new IndexOutOfBoundsException("Position out of bounds");
        }
        return board[r][c];
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
            throw new IllegalArgumentException("Cell at (" + row + "," + col + ") is not an EntityCell");
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
        Cell cell = getCell(position);
        if (!(cell instanceof EntityCell ec)) {
            throw new IllegalArgumentException("Cell at position is not an EntityCell");
        }
        return ec;
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
}