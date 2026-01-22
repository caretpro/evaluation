
package game.map;

import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.map.cells.TerminationCell;
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
     * Constructor to initialize the map with given rows and columns.
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
        // Initialize source and sink cells
        Random rng = new Random();
        TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
        Coordinate sourceCoord = sourceInfo.coord;
        Direction sourceDirection = sourceInfo.direction;
        sourceCell = new TerminationCell(sourceCoord, sourceDirection);
        cells[sourceCoord.row][sourceCoord.col] = sourceCell;
        TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
        Coordinate sinkCoord = sinkInfo.coord;
        Direction sinkDirection = sinkInfo.direction;
        sinkCell = new TerminationCell(sinkCoord, sinkDirection);
        cells[sinkCoord.row][sinkCoord.col] = sinkCell;
    }

    // Existing methods...
}