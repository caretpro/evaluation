
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
     * @throws IllegalArgumentException if the board dimensions are invalid, if there is not exactly one player,
     *                                  if there are no gems, or if some gems cannot be reached.
     */
    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (numRows <= 0 || numCols <= 0 || cells == null || cells.length != numRows) {
            throw new IllegalArgumentException();
        }

        final Cell[][] copiedBoard = new Cell[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            if (cells[row] == null || cells[row].length != numCols) {
                throw new IllegalArgumentException();
            }

            for (int col = 0; col < numCols; col++) {
                if (cells[row][col] == null) {
                    throw new IllegalArgumentException();
                }
                copiedBoard[row][col] = cells[row][col];
            }
        }

        this.numRows = numRows;
        this.numCols = numCols;
        this.board = copiedBoard;
        this.player = getSinglePlayer();

        if (getNumGems() == 0) {
            throw new IllegalArgumentException();
        }

        if (player.getOwner() == null || !isAllGemsReachable()) {
            throw new IllegalArgumentException();
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
        Player foundPlayer = null;

        for (final Cell[] row : board) {
            for (final Cell cell : row) {
                if (cell instanceof EntityCell entityCell
                        && entityCell.getEntity() instanceof Player currentPlayer) {
                    if (foundPlayer != null) {
                        throw new IllegalArgumentException();
                    }
                    foundPlayer = currentPlayer;
                }
            }
        }

        if (foundPlayer == null) {
            throw new IllegalArgumentException();
        }

        return foundPlayer;
    }

    /**
     * Retrieves the position of an entity cell by applying an offset.
     *
     * @param pos    Position.
     * @param offset Positional offset to apply.
     * @return The resulting position, or {@code null} if it is outside the board or is a wall.
     */
    @Nullable
    private Position getEntityCellByOffset(
            @NotNull final Position pos,
            @NotNull final PositionOffset offset) {
        final Position newPos = pos.offsetByOrNull(offset, getNumRows(), getNumCols());

        if (newPos == null || getCell(newPos) instanceof Wall) {
            return null;
        }

        return newPos;
    }

    /**
     * Gets all positions on which the player can stop.
     *
     * @param initialPosition Starting position.
     * @return All stoppable positions.
     */
    @NotNull
    private List<Position> getAllStoppablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> allStoppablePos = new ArrayList<>();
        final List<Position> posToTraverse = new ArrayList<>();
        posToTraverse.add(initialPosition);

        while (!posToTraverse.isEmpty()) {
            final Position nextPos = posToTraverse.remove(posToTraverse.size() - 1);

            if (!(getCell(nextPos) instanceof EntityCell) || allStoppablePos.contains(nextPos)) {
                continue;
            }

            allStoppablePos.add(nextPos);

            for (final Direction direction : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    final PositionOffset offset = new PositionOffset(
                            direction.getRowOffset() * i,
                            direction.getColOffset() * i);

                    final Position positionToAdd = getEntityCellByOffset(nextPos, offset);

                    if (positionToAdd == null) {
                        final int maxDistance = i - 1;

                        if (maxDistance > 0) {
                            final PositionOffset previousOffset = new PositionOffset(
                                    direction.getRowOffset() * maxDistance,
                                    direction.getColOffset() * maxDistance);

                            final Position positionBeforeWall =
                                    getEntityCellByOffset(nextPos, previousOffset);

                            if (positionBeforeWall != null
                                    && !posToTraverse.contains(positionBeforeWall)) {
                                posToTraverse.add(positionBeforeWall);
                            }
                        }

                        break;
                    }

                    if (getCell(positionToAdd) instanceof StopCell
                            || isBorderCell(positionToAdd, direction)) {
                        if (!posToTraverse.contains(positionToAdd)) {
                            posToTraverse.add(positionToAdd);
                        }
                    }
                }
            }
        }

        return Collections.unmodifiableList(allStoppablePos);
    }

    /**
     * Gets all positions reachable from the specified position.
     *
     * @param initialPosition Starting position.
     * @return All reachable positions.
     */
    @NotNull
    private List<Position> getAllReachablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> allReachablePos = new ArrayList<>();

        for (final Position reachablePos : getAllStoppablePositions(initialPosition)) {
            for (final Direction direction : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    final PositionOffset offset = new PositionOffset(
                            direction.getRowOffset() * i,
                            direction.getColOffset() * i);

                    final Position positionToAdd = getEntityCellByOffset(reachablePos, offset);

                    if (positionToAdd == null) {
                        break;
                    }

                    if (!allReachablePos.contains(positionToAdd)) {
                        allReachablePos.add(positionToAdd);
                    }
                }
            }
        }

        return Collections.unmodifiableList(allReachablePos);
    }

    /**
     * Checks whether the given cell is a border cell in the specified direction.
     *
     * @param cellPos Cell position.
     * @param dir     Direction of movement.
     * @return Whether the cell is at the relevant board border.
     */
    private boolean isBorderCell(
            @NotNull final Position cellPos,
            @NotNull final Direction dir) {
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
        final int expectedNumOfGems = getNumGems();
        final Position initialPosition = Objects.requireNonNull(getPlayer().getOwner()).getPosition();
        final List<Position> reachablePositions = getAllReachablePositions(initialPosition);

        int actualNumOfGems = 0;

        for (final Position position : reachablePositions) {
            final Cell cell = getCell(position);

            if (cell instanceof EntityCell entityCell
                    && entityCell.getEntity() instanceof Gem) {
                actualNumOfGems++;
            }
        }

        return expectedNumOfGems == actualNumOfGems;
    }

    /**
     * Returns the cells of a single row.
     *
     * @param row Row index.
     * @return A copy of the requested row.
     */
    @NotNull
    public Cell[] getRow(final int row) {
        checkRowIndex(row);
        return board[row].clone();
    }

    /**
     * Returns the cells of a single column.
     *
     * @param col Column index.
     * @return A copy of the requested column.
     */
    @NotNull
    public Cell[] getCol(final int col) {
        checkColIndex(col);

        final Cell[] result = new Cell[numRows];

        for (int row = 0; row < numRows; row++) {
            result[row] = board[row][col];
        }

        return result;
    }

    /**
     * Returns a single cell of the game board.
     *
     * @param row Row index.
     * @param col Column index.
     * @return The cell at the specified location.
     */
    @NotNull
    public Cell getCell(final int row, final int col) {
        checkRowIndex(row);
        checkColIndex(col);
        return board[row][col];
    }

    /**
     * Returns a single cell of the game board.
     *
     * @param position Position of the cell.
     * @return The cell at the specified location.
     */
    @NotNull
    public Cell getCell(@NotNull final Position position) {
        Objects.requireNonNull(position);
        return getCell(position.row(), position.col());
    }

    /**
     * Returns an entity cell on the game board.
     *
     * @param row Row index.
     * @param col Column index.
     * @return The entity cell at the specified location.
     * @throws IllegalArgumentException if the cell is not an entity cell.
     */
    @NotNull
    public EntityCell getEntityCell(final int row, final int col) {
        return getEntityCell(getCell(row, col));
    }

    /**
     * Returns an entity cell on the game board.
     *
     * @param position Position of the cell.
     * @return The entity cell at the specified location.
     * @throws IllegalArgumentException if the cell is not an entity cell.
     */
    @NotNull
    public EntityCell getEntityCell(@NotNull final Position position) {
        return getEntityCell(getCell(position));
    }

    private EntityCell getEntityCell(@NotNull final Cell cell) {
        if (!(cell instanceof EntityCell entityCell)) {
            throw new IllegalArgumentException();
        }

        return entityCell;
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
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * @return The number of gems still present in the game board.
     */
    public int getNumGems() {
        int gems = 0;

        for (final Cell[] row : board) {
            for (final Cell cell : row) {
                if (cell instanceof EntityCell entityCell
                        && entityCell.getEntity() instanceof Gem) {
                    gems++;
                }
            }
        }

        return gems;
    }

    private void checkRowIndex(final int row) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }
    }

    private void checkColIndex(final int col) {
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }
    }
}