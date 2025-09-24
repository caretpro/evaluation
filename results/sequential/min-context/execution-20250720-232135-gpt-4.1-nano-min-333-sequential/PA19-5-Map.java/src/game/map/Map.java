
package game.map;

import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.map.cells.TerminationCell;
import game.map.cells.Wall;
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
     * Constructor to create an empty map with specified rows and columns.
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
        TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.direction);
        cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
        TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.direction);
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
    }

    /**
     * Constructor to create a map with specified rows, columns, and cells.
     */
    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
        }
        TerminationCell foundSource = null;
        TerminationCell foundSink = null;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = this.cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell termCell = (TerminationCell) cell;
                    if (termCell.isSource()) {
                        if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                            throw new IllegalArgumentException("Source must not be on the edge");
                        }
                        Direction dir = termCell.getDirection();
                        Coordinate targetCoord = new Coordinate(r, c).add(dir.getOffset());
                        if (isInBounds(targetCoord) && cells[targetCoord.row][targetCoord.col] instanceof Wall) {
                            throw new IllegalArgumentException("Source cannot point into a wall");
                        }
                        if (foundSource != null) {
                            throw new IllegalArgumentException("Multiple source cells found");
                        }
                        foundSource = termCell;
                        sourceCell = termCell;
                    } else if (termCell.isSink()) {
                        if (!(r == 0 || r == rows - 1 || c == 0 || c == cols - 1)) {
                            throw new IllegalArgumentException("Sink must be on the edge");
                        }
                        Direction dir = termCell.getDirection();
                        Coordinate targetCoord = new Coordinate(r, c).add(dir.getOffset());
                        if (isInBounds(targetCoord)) {
                            throw new IllegalArgumentException("Sink must point outside the map");
                        }
                        if (foundSink != null) {
                            throw new IllegalArgumentException("Multiple sink cells found");
                        }
                        foundSink = termCell;
                        sinkCell = termCell;
                    }
                }
            }
        }
        if (foundSource == null) {
            throw new IllegalArgumentException("Source cell not found");
        }
        if (foundSink == null) {
            throw new IllegalArgumentException("Sink cell not found");
        }
    }

    // Existing methods...

    public boolean isInBounds(@NotNull final Coordinate coord) {
        return coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols;
    }

    // Rest of your class...
}