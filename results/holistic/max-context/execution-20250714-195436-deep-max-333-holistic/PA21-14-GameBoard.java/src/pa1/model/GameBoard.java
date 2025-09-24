
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameBoard {
    private final int numRows;
    private final int numCols;
    @NotNull
    private final Cell[][] board;
    @NotNull
    private final Player player;

    public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
        if (numRows != cells.length || numCols != cells[0].length) {
            throw new IllegalArgumentException("Dimensions don't match cells array");
        }

        this.numRows = numRows;
        this.numCols = numCols;
        this.board = new Cell[numRows][numCols];
        
        // Copy cells
        for (int i = 0; i < numRows; i++) {
            System.arraycopy(cells[i], 0, this.board[i], 0, numCols);
        }

        this.player = getSinglePlayer();

        if (getNumGems() == 0) {
            throw new IllegalArgumentException("No gems on board");
        }

        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Not all gems are reachable");
        }
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

    public Cell[] getRow(final int row) {
        Cell[] rowCells = new Cell[numCols];
        System.arraycopy(board[row], 0, rowCells, 0, numCols);
        return rowCells;
    }

    public Cell[] getCol(final int col) {
        Cell[] colCells = new Cell[numRows];
        for (int i = 0; i < numRows; i++) {
            colCells[i] = board[i][col];
        }
        return colCells;
    }

    public Cell getCell(final int row, final int col) {
        return board[row][col];
    }

    public Cell getCell(final Position position) {
        return board[position.row()][position.col()];
    }

    public EntityCell getEntityCell(final int row, final int col) {
        Cell cell = getCell(row, col);
        if (!(cell instanceof EntityCell)) {
            throw new IllegalArgumentException("Cell is not an EntityCell");
        }
        return (EntityCell) cell;
    }

    public EntityCell getEntityCell(final Position position) {
        return getEntityCell(position.row(), position.col());
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Player getPlayer() {
        return player;
    }

    public int getNumGems() {
        int count = 0;
        for (Cell[] row : board) {
            for (Cell cell : row) {
                if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
                    count++;
                }
            }
        }
        return count;
    }

    // ... [rest of the existing methods remain unchanged] ...
}