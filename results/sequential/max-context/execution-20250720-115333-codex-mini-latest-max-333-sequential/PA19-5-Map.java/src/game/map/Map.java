
// File: game/map/Map.java
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
     * Constructs a map from a map string.
     * <p>
     * This is a convenience method for unit testing.
     * </p>
     *
     * @param rows     Number of rows.
     * @param cols     Number of columns.
     * @param cellsRep String representation of the map, with columns delimited by {@code '\n'}.
     * @return A map with the cells set from {@code cellsRep}.
     * @throws IllegalArgumentException If the map is incorrectly formatted.
     */
    @NotNull
    static Map fromString(int rows, int cols, @NotNull String cellsRep) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    // … other methods unchanged (tryPlacePipe, display, fillTiles, checkPath, etc.) …

    /**
     * Creates a map with size of rows x cols.
     * <p>
     * The map should only contain one source tile in any non-edge cell.
     * The map should only contain one sink tile in any edge cell.
     * The source tile must not point into a wall.
     * The sink tile must point outside the map.
     * </p>
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
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    cells[r][c] = new Wall(new Coordinate(r, c));
                } else {
                    cells[r][c] = new FillableCell(new Coordinate(r, c));
                }
            }
        }
        TerminationCell.CreateInfo srcInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(srcInfo.coord, srcInfo.dir, TerminationCell.Type.SOURCE);
        cells[srcInfo.coord.row][srcInfo.coord.col] = sourceCell;
        TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.dir, TerminationCell.Type.SINK);
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
    }

    /**
     * Creates a map with the given cells.
     * <p>
     * The map should only contain one source tile in any non-edge cell.
     * The map should only contain one sink tile in any edge cell.
     * The source tile must not point into a wall.
     * The sink tile must point outside the map.
     * </p>
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param cells Cells to fill the map.
     */
    public Map(int rows, int cols, Cell[][] cells) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("rows and cols must be positive");
        }
        if (cells == null || cells.length != rows) {
            throw new IllegalArgumentException("Invalid cells: row count mismatch");
        }
        for (Cell[] row : cells) {
            if (row == null || row.length != cols) {
                throw new IllegalArgumentException("Invalid cells: column count mismatch");
            }
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;

        TerminationCell foundSource = null;
        TerminationCell foundSink = null;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell tc) {
                    boolean isEdge = (r == 0 || r == rows - 1 || c == 0 || c == cols - 1);
                    if (tc.type == TerminationCell.Type.SOURCE) {
                        if (isEdge) {
                            throw new IllegalArgumentException("Source cannot be on edge: " + tc.coord);
                        }
                        Coordinate adj = tc.coord.add(tc.pointingTo.getOffset());
                        if (cells[adj.row][adj.col] instanceof Wall) {
                            throw new IllegalArgumentException("Source points into wall at " + adj);
                        }
                        if (foundSource != null) {
                            throw new IllegalArgumentException("Multiple sources found");
                        }
                        foundSource = tc;
                    } else {
                        if (!isEdge) {
                            throw new IllegalArgumentException("Sink must be on edge: " + tc.coord);
                        }
                        Coordinate adj = tc.coord.add(tc.pointingTo.getOffset());
                        if (adj.row >= 0 && adj.row < rows && adj.col >= 0 && adj.col < cols) {
                            throw new IllegalArgumentException("Sink points inside map at " + adj);
                        }
                        if (foundSink != null) {
                            throw new IllegalArgumentException("Multiple sinks found");
                        }
                        foundSink = tc;
                    }
                }
            }
        }
        if (foundSource == null) {
            throw new IllegalArgumentException("No source cell found");
        }
        if (foundSink == null) {
            throw new IllegalArgumentException("No sink cell found");
        }
        this.sourceCell = foundSource;
        this.sinkCell = foundSink;
    }

    // … (rest of class unchanged) …

    /**
     * <p>
     * Hint: The game is lost when a round ends and no pipes are filled during the round. Is
     * there a way to check whether pipes are filled during a round?
     * </p>
     *
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        return prevFilledDistance != null && filledTiles.size() == prevFilledTiles;
    }

    // … helper methods (generateStartCellInfo, generateEndCellInfo, etc.) …

}