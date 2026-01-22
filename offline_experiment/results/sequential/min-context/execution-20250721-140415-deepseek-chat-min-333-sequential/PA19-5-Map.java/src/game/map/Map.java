
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
    @NotNull final Cell[][] cells;
    private TerminationCell sourceCell;
    private TerminationCell sinkCell;
    @NotNull private final Set<Coordinate> filledTiles = new HashSet<>();
    private int prevFilledTiles = 0;
    private Integer prevFilledDistance;

    public Map(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        // Initialize cells and termination cells
    }

    public Map(int rows, int cols, @NotNull Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        // Validate termination cells
    }

    // Rest of the class implementation...
}