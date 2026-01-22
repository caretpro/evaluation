
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

    public Map(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new Wall(new Coordinate(i, j));
            }
        }
        TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.dir, TerminationCell.Type.SOURCE);
        cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
        TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.dir, TerminationCell.Type.SINK);
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        int sourceCount = 0;
        int sinkCount = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                if (cell instanceof TerminationCell) {
                    TerminationCell termCell = (TerminationCell) cell;
                    if (termCell.type == TerminationCell.Type.SOURCE) {
                        sourceCount++;
                        sourceCell = termCell;
                        if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                            throw new IllegalArgumentException("Source must be in non-edge cell");
                        }
                        Coordinate adjacent = termCell.coord.add(termCell.pointingTo.getOffset());
                        if (adjacent.row < 0 || adjacent.row >= rows || adjacent.col < 0 || adjacent.col >= cols
                                || cells[adjacent.row][adjacent.col] instanceof Wall) {
                            throw new IllegalArgumentException("Source must not point into wall");
                        }
                    } else if (termCell.type == TerminationCell.Type.SINK) {
                        sinkCount++;
                        sinkCell = termCell;
                        if (i != 0 && i != rows - 1 && j != 0 && j != cols - 1) {
                            throw new IllegalArgumentException("Sink must be in edge cell");
                        }
                        Coordinate outside = termCell.coord.add(termCell.pointingTo.getOffset());
                        if (outside.row >= 0 && outside.row < rows && outside.col >= 0 && outside.col < cols) {
                            throw new IllegalArgumentException("Sink must point outside map");
                        }
                    }
                }
            }
        }
        if (sourceCount != 1) {
            throw new IllegalArgumentException("Map must contain exactly one source cell");
        }
        if (sinkCount != 1) {
            throw new IllegalArgumentException("Map must contain exactly one sink cell");
        }
    }

    // Rest of the class implementation...
}