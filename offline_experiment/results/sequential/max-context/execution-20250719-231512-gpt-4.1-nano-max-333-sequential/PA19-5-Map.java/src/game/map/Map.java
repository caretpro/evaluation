
package game.map;

import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.map.cells.TerminationCell;
import game.map.cells.Wall;
import io.Deserializer;
import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;

import java.util.*;

/**
 * Map of the game.
 */
public class Map {

    public final int rows;
    public final int cols;
    @NotNull
    final Cell[][] cells;

    private TerminationCell sourceCell;
    private TerminationCell sinkCell;

    @NotNull
    private final Set<Coordinate> filledTiles = new HashSet<>();
    private int prevFilledTiles = 0;

    /**
     * Constructor with only rows and columns, initializes an empty map.
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
        TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.dir, TerminationCell.Type.SOURCE);
        cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
        TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.dir, TerminationCell.Type.SINK);
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
    }

    /**
     * Constructor with explicit cells array.
     */
    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
        }
        int sourceCount = 0;
        int sinkCount = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = this.cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell term = (TerminationCell) cell;
                    if (term.type == TerminationCell.Type.SOURCE) {
                        sourceCount++;
                        sourceCell = term;
                    } else if (term.type == TerminationCell.Type.SINK) {
                        sinkCount++;
                        sinkCell = term;
                    }
                }
            }
        }
        if (sourceCount != 1 || sinkCount != 1) {
            throw new IllegalArgumentException("Map must contain exactly one source and one sink");
        }
    }

    // Existing methods...

    // (Other methods remain unchanged)
}