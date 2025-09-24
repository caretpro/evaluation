
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The game board, holding the 2D grid of entity‐cells (walls, floors, goals, etc.),
 * and providing the logic for reachability, movement, and game‐state enforcement.
 */
public class GameBoard {
    private final int rows;
    private final int cols;
    private final EntityCell[][] cells;

    /**
     * Constructs a GameBoard from the given rows of cells.
     * Throws IllegalArgumentException if:
     * - rows or cols are non‐positive
     * - input is ragged (rows with different column counts)
     * - any cell row/column is null
     * - any gem on the board is not reachable from the player start
     *
     * @param board Rows of {@link EntityCell} making up the board.
     */
    public GameBoard(@NotNull final List<List<EntityCell>> board) {
        // Validate top‐level structure
        if (board.isEmpty()) {
            throw new IllegalArgumentException("Board must have at least one row");
        }

        this.rows = board.size();
        this.cols = board.get(0).size();
        if (this.cols == 0) {
            throw new IllegalArgumentException("Board must have at least one column");
        }

        // Instantiate and validate each row
        this.cells = new EntityCell[rows][cols];
        for (int r = 0; r < rows; r++) {
            List<EntityCell> row = board.get(r);
            if (row == null || row.size() != cols) {
                throw new IllegalArgumentException("All rows must be non‐null and of length " + cols);
            }
            for (int c = 0; c < cols; c++) {
                EntityCell cell = row.get(c);
                if (cell == null) {
                    throw new IllegalArgumentException("Cells must be non‐null");
                }
                cells[r][c] = cell;
            }
        }

        // Check gem‐reachability BEFORE finalizing construction
        if (!areAllGemsReachable()) {
            throw new IllegalArgumentException("Unreachable gem detected");
        }
    }

    /** @return The number of rows in this board. */
    public int getNumRows() {
        return rows;
    }

    /** @return The number of columns in this board. */
    public int getNumCols() {
        return cols;
    }

    /**
     * Returns the cell at the given row/column.
     * @throws IndexOutOfBoundsException for invalid indices.
     */
    public @NotNull EntityCell getCell(final int row, final int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("Invalid cell coordinates");
        }
        return cells[row][col];
    }

    /**
     * Returns the cell at the given position.
     * @throws IndexOutOfBoundsException for invalid position.
     */
    public @NotNull EntityCell getCell(final Position pos) {
        Objects.requireNonNull(pos, "Position must not be null");
        return getCell(pos.row(), pos.col());
    }

    // --------------------------------------------------
    // Reachability logic: no more null‐triggered NPEs.
    // --------------------------------------------------

    /** Checks whether every gem is reachable from the player's start cell. */
    private boolean areAllGemsReachable() {
        // Find the player cell
        Position start = findPlayerPosition();
        if (start == null) {
            // No player => no reachability to check
            return true;
        }

        // BFS from player over non‐wall cells
        boolean[][] visited = new boolean[rows][cols];
        Deque<Position> queue = new ArrayDeque<>();
        queue.add(start);
        visited[start.row()][start.col()] = true;

        while (!queue.isEmpty()) {
            Position p = queue.removeFirst();
            for (Position n : p.adjacent()) {
                if (inBounds(n) && !visited[n.row()][n.col()] && !getCell(n).isWall()) {
                    visited[n.row()][n.col()] = true;
                    queue.addLast(n);
                }
            }
        }

        // Verify every gem cell was visited
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                EntityCell cell = cells[r][c];
                if (cell.hasGem() && !visited[r][c]) {
                    return false;
                }
            }
        }
        return true;
    }

    /** @return position of the cell containing the player, or null if none present. */
    private Position findPlayerPosition() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c].hasPlayer()) {
                    return Position.of(r, c);
                }
            }
        }
        return null;
    }

    private boolean inBounds(Position p) {
        return p.row() >= 0 && p.row() < rows && p.col() >= 0 && p.col() < cols;
    }

    // ... rest of GameBoard methods (movement, undo, redo, etc.) ...
}