
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

    private final int numRows;
    private final int numCols;

    @NotNull
    private final Cell[][] board;

    @NotNull
    private final Player player;

    /**
     * Creates an instance using the provided creation parameters.
     *
     * @param numRows The number of rows in the game board.
     * @param numCols The number of columns in the game board.
     * @param cells   The initial values of cells.
     * @throws IllegalArgumentException if the board dimensions are invalid, there is not exactly one player,
     *                                  there are no gems, or some gems cannot be reached.
     */
    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (numRows <= 0 || numCols <= 0 || cells == null || cells.length != numRows) {
            throw new IllegalArgumentException();
        }

        final Cell[][] copiedBoard = new Cell[numRows][numCols];

        for (int row = 0; row < numRows; ++row) {
            if (cells[row] == null || cells[row].length != numCols) {
                throw new IllegalArgumentException();
            }

            for (int col = 0; col < numCols; ++col) {
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

        if (getNumGems() == 0 || !isAllGemsReachable()) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks that a single player exists on the game board.
     *
     * @return The single player on the game board.
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
     * Retrieves the position of a non-wall cell by applying an offset.
     *
     * @param pos    Position.
     * @param offset Positional offset.
     * @return The resulting position, or {@code null} if it is out of bounds or is a wall.
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

        final List<Position> allStoppablePositions = new ArrayList<>();
        final List<Position> positionsToTraverse = new ArrayList<>();
        positionsToTraverse.add(initialPosition);

        while (!positionsToTraverse.isEmpty()) {
            final Position nextPosition =
                    positionsToTraverse.remove(positionsToTraverse.size() - 1);

            if (!(getCell(nextPosition) instanceof EntityCell)
                    || allStoppablePositions.contains(nextPosition)) {
                continue;
            }

            allStoppablePositions.add(nextPosition);

            for (final Direction direction : Direction.values()) {
                for (int distance = 0;
                     distance < Math.max(getNumRows(), getNumCols());
                     ++distance) {

                    final PositionOffset offset = new PositionOffset(
                            direction.getRowOffset() * distance,
                            direction.getColOffset() * distance);

                    final Position position = getEntityCellByOffset(nextPosition, offset);

                    if (position == null) {
                        final int maximumDistance = distance - 1;

                        if (maximumDistance > 0) {
                            final Position previousPosition = getEntityCellByOffset(
                                    nextPosition,
                                    new PositionOffset(
                                            direction.getRowOffset() * maximumDistance,
                                            direction.getColOffset() * maximumDistance));

                            if (previousPosition != null
                                    && !positionsToTraverse.contains(previousPosition)) {
                                positionsToTraverse.add(previousPosition);
                            }
                        }

                        break;
                    }

                    if (getCell(position) instanceof StopCell
                            || isBorderCell(position, direction)) {
                        if (!positionsToTraverse.contains(position)) {
                            positionsToTraverse.add(position);
                        }
                    }
                }
            }
        }

        return Collections.unmodifiableList(allStoppablePositions);
    }

    /**
     * Gets all positions reachable from a starting position.
     *
     * @param initialPosition Starting position.
     * @return All reachable positions.
     */
    @NotNull
    private List<Position> getAllReachablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> allReachablePositions = new ArrayList<>();

        for (final Position stoppablePosition : getAllStoppablePositions(initialPosition)) {
            for (final Direction direction : Direction.values()) {
                for (int distance = 0;
                     distance < Math.max(getNumRows(), getNumCols());
                     ++distance) {

                    final Position position = getEntityCellByOffset(
                            stoppablePosition,
                            new PositionOffset(
                                    direction.getRowOffset() * distance,
                                    direction.getColOffset() * distance));

                    if (position == null) {
                        break;
                    }

                    if (!allReachablePositions.contains(position)) {
                        allReachablePositions.add(position);
                    }
                }
            }
        }

        return Collections.unmodifiableList(allReachablePositions);
    }

    private boolean isBorderCell(
            @NotNull final Position cellPosition,
            @NotNull final Direction direction) {
        return switch (direction) {
            case UP -> cellPosition.row() == 0;
            case DOWN -> cellPosition.row() == getNumRows() - 1;
            case LEFT -> cellPosition.col() == 0;
            case RIGHT -> cellPosition.col() == getNumCols() - 1;
        };
    }

    private boolean isAllGemsReachable() {
        final Position initialPosition =
                Objects.requireNonNull(getPlayer().getOwner()).getPosition();

        final List<Position> reachablePositions =
                getAllReachablePositions(initialPosition);

        int reachableGemCount = 0;

        for (final Position position : reachablePositions) {
            final Cell cell = getCell(position);

            if (cell instanceof EntityCell entityCell
                    && entityCell.getEntity() instanceof Gem) {
                ++reachableGemCount;
            }
        }

        return reachableGemCount == getNumGems();
    }

    /**
     * Returns a copy of a row.
     *
     * @param row Row index.
     * @return Cells in the requested row.
     */
    @NotNull
    public Cell[] getRow(final int row) {
        return board[row].clone();
    }

    /**
     * Returns a copy of a column.
     *
     * @param col Column index.
     * @return Cells in the requested column.
     */
    @NotNull
    public Cell[] getCol(final int col) {
        final Cell[] result = new Cell[numRows];

        for (int row = 0; row < numRows; ++row) {
            result[row] = board[row][col];
        }

        return result;
    }

    /**
     * Returns a cell by row and column.
     *
     * @param row Row index.
     * @param col Column index.
     * @return The cell at the specified location.
     */
    @NotNull
    public Cell getCell(final int row, final int col) {
        return board[row][col];
    }

    /**
     * Returns a cell by position.
     *
     * @param position Cell position.
     * @return The cell at the specified location.
     */
    @NotNull
    public Cell getCell(@NotNull final Position position) {
        Objects.requireNonNull(position);
        return getCell(position.row(), position.col());
    }

    /**
     * Returns an entity cell by row and column.
     *
     * @param row Row index.
     * @param col Column index.
     * @return The entity cell at the specified location.
     * @throws IllegalArgumentException if the cell is not an entity cell.
     */
    @NotNull
    public EntityCell getEntityCell(final int row, final int col) {
        final Cell cell = getCell(row, col);

        if (!(cell instanceof EntityCell entityCell)) {
            throw new IllegalArgumentException();
        }

        return entityCell;
    }

    /**
     * Returns an entity cell by position.
     *
     * @param position Cell position.
     * @return The entity cell at the specified location.
     * @throws IllegalArgumentException if the cell is not an entity cell.
     */
    @NotNull
    public EntityCell getEntityCell(@NotNull final Position position) {
        return getEntityCell(position.row(), position.col());
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * Counts the gems currently present on the board.
     *
     * @return Number of gems.
     */
    public int getNumGems() {
        int gemCount = 0;

        for (final Cell[] row : board) {
            for (final Cell cell : row) {
                if (cell instanceof EntityCell entityCell
                        && entityCell.getEntity() instanceof Gem) {
                    ++gemCount;
                }
            }
        }

        return gemCount;
    }
}