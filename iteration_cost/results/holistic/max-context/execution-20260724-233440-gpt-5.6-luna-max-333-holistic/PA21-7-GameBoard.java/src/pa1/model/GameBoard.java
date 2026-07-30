
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
 * The top-left hand corner of the game board is the origin of the board (0, 0).
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
     * @throws IllegalArgumentException if the board dimensions are invalid, if there is not exactly one player,
     *                                  if there are no gems, or if some gems cannot be reached.
     */
    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (numRows <= 0 || numCols <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive.");
        }
        if (cells == null || cells.length != numRows) {
            throw new IllegalArgumentException("The number of rows does not match the board data.");
        }

        final Cell[][] copiedBoard = new Cell[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            if (cells[row] == null || cells[row].length != numCols) {
                throw new IllegalArgumentException("The number of columns does not match the board data.");
            }

            for (int col = 0; col < numCols; col++) {
                if (cells[row][col] == null) {
                    throw new IllegalArgumentException("Board cells cannot be null.");
                }
                copiedBoard[row][col] = cells[row][col];
            }
        }

        this.numRows = numRows;
        this.numCols = numCols;
        this.board = copiedBoard;
        this.player = getSinglePlayer();

        if (getNumGems() == 0) {
            throw new IllegalArgumentException("The board must contain at least one gem.");
        }

        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Not all gems are reachable by the player.");
        }
    }

    /**
     * Checks that a single player exists on the game board.
     *
     * @return The single player on the board.
     * @throws IllegalArgumentException if there are zero or multiple players.
     */
    @NotNull
    private Player getSinglePlayer() {
        Player foundPlayer = null;

        for (final Cell[] row : board) {
            for (final Cell cell : row) {
                if (cell instanceof EntityCell entityCell
                        && entityCell.getEntity() instanceof Player currentPlayer) {
                    if (foundPlayer != null) {
                        throw new IllegalArgumentException("The board must contain exactly one player.");
                    }
                    foundPlayer = currentPlayer;
                }
            }
        }

        if (foundPlayer == null) {
            throw new IllegalArgumentException("The board must contain exactly one player.");
        }

        return foundPlayer;
    }

    /**
     * Retrieves the position of an entity cell by applying an offset.
     *
     * @param pos    Starting position.
     * @param offset Positional offset to apply.
     * @return The resulting position, or {@code null} if it is out of bounds or is a wall.
     */
    @Nullable
    private Position getEntityCellByOffset(
            @NotNull final Position pos,
            @NotNull final PositionOffset offset) {
        final Position newPosition = pos.offsetByOrNull(offset, numRows, numCols);

        if (newPosition == null || getCell(newPosition) instanceof Wall) {
            return null;
        }

        return newPosition;
    }

    /**
     * Gets all positions where the player can stop from the specified position.
     *
     * @param initialPosition Starting position.
     * @return All stoppable positions.
     */
    @NotNull
    private List<Position> getAllStoppablePositions(@NotNull final Position initialPosition) {
        Objects.requireNonNull(initialPosition);

        final List<Position> stoppablePositions = new ArrayList<>();
        final List<Position> positionsToTraverse = new ArrayList<>();
        positionsToTraverse.add(initialPosition);

        while (!positionsToTraverse.isEmpty()) {
            final Position nextPosition =
                    positionsToTraverse.remove(positionsToTraverse.size() - 1);

            if (!(getCell(nextPosition) instanceof EntityCell)
                    || stoppablePositions.contains(nextPosition)) {
                continue;
            }

            stoppablePositions.add(nextPosition);

            for (final Direction direction : Direction.values()) {
                for (int distance = 0; distance < Math.max(numRows, numCols); distance++) {
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

        return Collections.unmodifiableList(stoppablePositions);
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

        final List<Position> reachablePositions = new ArrayList<>();

        for (final Position stoppablePosition : getAllStoppablePositions(initialPosition)) {
            for (final Direction direction : Direction.values()) {
                for (int distance = 0; distance < Math.max(numRows, numCols); distance++) {
                    final Position position = getEntityCellByOffset(
                            stoppablePosition,
                            new PositionOffset(
                                    direction.getRowOffset() * distance,
                                    direction.getColOffset() * distance));

                    if (position == null) {
                        break;
                    }

                    if (!reachablePositions.contains(position)) {
                        reachablePositions.add(position);
                    }
                }
            }
        }

        return Collections.unmodifiableList(reachablePositions);
    }

    /**
     * Checks whether a position is a border cell in the specified direction.
     */
    private boolean isBorderCell(
            @NotNull final Position cellPosition,
            @NotNull final Direction direction) {
        return switch (direction) {
            case UP -> cellPosition.row() == 0;
            case DOWN -> cellPosition.row() == numRows - 1;
            case LEFT -> cellPosition.col() == 0;
            case RIGHT -> cellPosition.col() == numCols - 1;
        };
    }

    /**
     * Checks whether all gems are reachable from the player's initial position.
     */
    private boolean isAllGemsReachable() {
        final Position initialPosition =
                Objects.requireNonNull(player.getOwner()).getPosition();

        int reachableGemCount = 0;

        for (final Position position : getAllReachablePositions(initialPosition)) {
            final Cell cell = getCell(position);

            if (cell instanceof EntityCell entityCell
                    && entityCell.getEntity() instanceof Gem) {
                reachableGemCount++;
            }
        }

        return reachableGemCount == getNumGems();
    }

    /**
     * Returns a copy of a row of the board.
     *
     * @param row Row index.
     * @return Cells in the requested row.
     */
    @NotNull
    public Cell[] getRow(final int row) {
        checkRowIndex(row);
        return board[row].clone();
    }

    /**
     * Returns a copy of a column of the board.
     *
     * @param col Column index.
     * @return Cells in the requested column.
     */
    @NotNull
    public Cell[] getCol(final int col) {
        checkColumnIndex(col);

        final Cell[] result = new Cell[numRows];
        for (int row = 0; row < numRows; row++) {
            result[row] = board[row][col];
        }

        return result;
    }

    /**
     * Returns a cell at the specified coordinates.
     */
    @NotNull
    public Cell getCell(final int row, final int col) {
        checkRowIndex(row);
        checkColumnIndex(col);
        return board[row][col];
    }

    /**
     * Returns a cell at the specified position.
     */
    @NotNull
    public Cell getCell(@NotNull final Position position) {
        Objects.requireNonNull(position);
        return getCell(position.row(), position.col());
    }

    /**
     * Returns an entity cell at the specified coordinates.
     *
     * @throws IllegalArgumentException if the cell is not an entity cell.
     */
    @NotNull
    public EntityCell getEntityCell(final int row, final int col) {
        return asEntityCell(getCell(row, col));
    }

    /**
     * Returns an entity cell at the specified position.
     *
     * @throws IllegalArgumentException if the cell is not an entity cell.
     */
    @NotNull
    public EntityCell getEntityCell(@NotNull final Position position) {
        return asEntityCell(getCell(position));
    }

    @NotNull
    private EntityCell asEntityCell(@NotNull final Cell cell) {
        if (!(cell instanceof EntityCell entityCell)) {
            throw new IllegalArgumentException("The requested cell is not an entity cell.");
        }
        return entityCell;
    }

    private void checkRowIndex(final int row) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("Invalid row index: " + row);
        }
    }

    private void checkColumnIndex(final int col) {
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("Invalid column index: " + col);
        }
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
     * @return The number of gems currently present on the board.
     */
    public int getNumGems() {
        int gemCount = 0;

        for (final Cell[] row : board) {
            for (final Cell cell : row) {
                if (cell instanceof EntityCell entityCell
                        && entityCell.getEntity() instanceof Gem) {
                    gemCount++;
                }
            }
        }

        return gemCount;
    }
}