
package game.map;

import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.map.cells.TerminationCell;
import org.jetbrains.annotations.NotNull;

/**
 * Map of the game.
 */
public class Map {
    private final int rows;
    private final int cols;
    @NotNull
    final Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;

    /**
     * Constructor that initializes an empty map with given rows and columns.
     * All cells are initialized as FillableCell.
     */
    public Map(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new FillableCell();
            }
        }
        this.sourceCell = null;
        this.sinkCell = null;
    }

    /**
     * Constructor that initializes the map with specified cells.
     */
    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
        }
        this.sourceCell = null;
        this.sinkCell = null;
    }

    // Existing methods...
}