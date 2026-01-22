
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
        sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.direction, TerminationCell.Type.SOURCE);
        cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
        TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.direction, TerminationCell.Type.SINK);
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        // Rest of the validation logic remains the same
    }

    // Keep all other existing methods unchanged
}