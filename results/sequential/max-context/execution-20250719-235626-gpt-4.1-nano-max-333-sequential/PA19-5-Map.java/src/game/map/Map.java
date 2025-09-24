
package game.map;

import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.map.cells.TerminationCell;
import game.map.cells.Wall;
import game.pipes.Pipe;
import io.Deserializer;
import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;
import util.StringUtils;

import java.util.*;

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

    @NotNull
    private final Set<Coordinate> filledTiles = new HashSet<>();
    private int prevFilledTiles = 0;
    private Integer prevFilledDistance;

    /**
     * Constructor accepting only rows and columns, initializes the map with empty fillable cells.
     *
     * @param rows Number of rows.
     * @param cols Number of columns.
     */
    public Map(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new FillableCell(new Coordinate(r, c));
            }
        }
        this.sourceCell = null;
        this.sinkCell = null;
    }

    /**
     * Constructor accepting rows, columns, and a pre-defined cell grid.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param cells 2D array of cells representing the map.
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
        // Optionally, initialize source and sink references if needed
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = this.cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell term = (TerminationCell) cell;
                    if (term.type == TerminationCell.Type.SOURCE) {
                        this.sourceCell = term;
                    } else if (term.type == TerminationCell.Type.SINK) {
                        this.sinkCell = term;
                    }
                }
            }
        }
    }

    // Existing methods...

    public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    // ... rest of your existing class code ...
}