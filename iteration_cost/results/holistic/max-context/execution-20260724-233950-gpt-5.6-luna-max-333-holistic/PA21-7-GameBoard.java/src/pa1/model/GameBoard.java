
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
        if (numRows <= 0 || numCols <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive.");
        }

        Objects.requireNonNull(cells, "cells cannot be null");

        if (numRows != cells.length) {
            throw new IllegalArgumentException("The number of rows does not match cells.length.");
        }

        final Cell[][] copiedBoard = new Cell[numRows][numCols];

        for (int row = 0; row < numRows; row++) {
            if (cells[row] == null || cells[row].length != numCols) {
                throw new IllegalArgumentException("The number of columns does not match the board dimensions.");
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

    @NotNull
    private Player getSinglePlayer() {
        Player foundPlayer = null;

        for (final Cell[] row : board) {
            for (final Cell cell : row) {
                if (cell instanceof EntityCell entityCell
                        && entityCell.getEntity() instanceof Player currentPlayer) {
                    if (foundPlayer != null) {
                        throw new IllegalArgumentException("The board contains more than one player.");
                    }
                    foundPlayer = currentPlayer;
                }
            }
        }

        if (foundPlayer == null) {
            throw new IllegalArgumentException("The board does not contain a player.");
        }

        return foundPlayer;
    }

    @Nullable
    private Position getEntityCellByOffset(
            @NotNull final Position pos,
            @NotNull final PositionOffset offset) {
        Objects.requireNonNull(pos);
        Objects.requireNonNull(offset);

        final Position newPos = pos.offsetByOrNull(offset, getNumRows(), getNumCols());

        if (newPos == null || getCell(newPos) instanceof Wall) {
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
            final Position nextPos = posToTraverse.remove(posToTraverse.size() - 1);

            if (!(getCell(nextPos) instanceof EntityCell)
                    || allStoppablePos.contains(nextPos)) {
                continue;
            }

            allStoppablePos.add(nextPos);

            for (final Direction dir : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    final PositionOffset posOffset = new PositionOffset(
                            dir.getRowOffset() * i,
                            dir.getColOffset() * i
                    );

                    final Position posToAdd = getEntityCellByOffset(nextPos, posOffset);

                    if (posToAdd == null) {
                        final int maxDist = i - 1;

                        if (maxDist > 0) {
                            final PositionOffset posOffsetBeforeThis = new PositionOffset(
                                    dir.getRowOffset() * maxDist,
                                    dir.getColOffset() * maxDist
                            );

                            final Position posBeforeThis =
                                    getEntityCellByOffset(nextPos, posOffsetBeforeThis);

                            if (posBeforeThis != null && !posToTraverse.contains(posBeforeThis)) {
                                posToTraverse.add(posBeforeThis);
                            }
                        }

                        break;
                    }

                    if (getCell(posToAdd) instanceof StopCell
                            || isBorderCell(posToAdd, dir)) {
                        if (!posToTraverse.contains(posToAdd)) {
                            posToTraverse.add(posToAdd);
                        }
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

        for (final Position reachablePos : allStoppablePos) {
            for (final Direction dir : Direction.values()) {
                for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
                    final PositionOffset posOffset = new PositionOffset(
                            dir.getRowOffset() * i,
                            dir.getColOffset() * i
                    );

                    final Position posToAdd = getEntityCellByOffset(reachablePos, posOffset);

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

    private boolean isAllGemsReachable() {
        final int expectedNumOfGems = getNumGems();
        final EntityCell playerOwner = Objects.requireNonNull(getPlayer().getOwner());
        final List<Position> playerReachableCells =
                getAllReachablePositions(playerOwner.getPosition());

        int actualNumOfGems = 0;

        for (final Position pos : playerReachableCells) {
            final Cell cell = getCell(pos);

            if (cell instanceof EntityCell entityCell
                    && entityCell.getEntity() instanceof Gem) {
                actualNumOfGems++;
            }
        }

        return expectedNumOfGems == actualNumOfGems;
    }

    /**
     * Returns a copy of a row of the board.
     *
     * @param row Row index.
     * @return The cells in the requested row.
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
     * @return The cells in the requested column.
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
     * Returns a single cell of the board.
     *
     * @param row Row index.
     * @param col Column index.
     * @return The cell at the requested location.
     */
    @NotNull
    public Cell getCell(final int row, final int col) {
        checkRowIndex(row);
        checkColumnIndex(col);
        return board[row][col];
    }

    /**
     * Returns a single cell of the board.
     *
     * @param position Position of the cell.
     * @return The cell at the requested location.
     */
    @NotNull
    public Cell getCell(@NotNull final Position position) {
        Objects.requireNonNull(position);
        return getCell(position.row(), position.col());
    }

    /**
     * Returns an entity cell of the board.
     *
     * @param row Row index.
     * @param col Column index.
     * @return The entity cell at the requested location.
     * @throws IllegalArgumentException if the cell is not an entity cell.
     */
    @NotNull
    public EntityCell getEntityCell(final int row, final int col) {
        final Cell cell = getCell(row, col);

        if (!(cell instanceof EntityCell entityCell)) {
            throw new IllegalArgumentException("The specified cell is not an entity cell.");
        }

        return entityCell;
    }

    /**
     * Returns an entity cell of the board.
     *
     * @param position Position of the cell.
     * @return The entity cell at the requested location.
     * @throws IllegalArgumentException if the cell is not an entity cell.
     */
    @NotNull
    public EntityCell getEntityCell(@NotNull final Position position) {
        return getEntityCell(
                Objects.requireNonNull(position).row(),
                position.col()
        );
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

    public int getNumGems() {
        int numGems = 0;

        for (final Cell[] row : board) {
            for (final Cell cell : row) {
                if (cell instanceof EntityCell entityCell
                        && entityCell.getEntity() instanceof Gem) {
                    numGems++;
                }
            }
        }

        return numGems;
    }

    private void checkRowIndex(final int row) {
        if (row < 0 || row >= numRows) {
            throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
        }
    }

    private void checkColumnIndex(final int col) {
        if (col < 0 || col >= numCols) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }
    }
}