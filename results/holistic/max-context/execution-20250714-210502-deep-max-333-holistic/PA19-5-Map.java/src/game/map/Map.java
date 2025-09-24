
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

        // Initialize empty cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new FillableCell(new Coordinate(i, j));
            }
        }

        // Generate source and sink
        var sourceInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.dir, TerminationCell.Type.SOURCE);
        cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;

        var sinkInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.dir, TerminationCell.Type.SINK);
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;

        // Find source and sink cells
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationCell.Type.SOURCE) {
                        sourceCell = tc;
                    } else if (tc.type == TerminationCell.Type.SINK) {
                        sinkCell = tc;
                    }
                }
            }
        }

        if (sourceCell == null || sinkCell == null) {
            throw new IllegalArgumentException("Map must contain exactly one source and one sink cell");
        }
    }

    boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell)) {
            return false;
        }

        FillableCell fillableCell = (FillableCell) cell;
        if (fillableCell.getPipe() != null) {
            return false;
        }

        fillableCell.setPipe(p);
        return true;
    }

    public void undo(final Coordinate coord) {
        if (coord.row < 0 || coord.row >= rows || coord.col < 0 || coord.col >= cols) {
            throw new IllegalArgumentException("Coordinate out of bounds");
        }

        Cell cell = cells[coord.row][coord.col];
        if (!(cell instanceof FillableCell)) {
            throw new IllegalArgumentException("Cannot undo non-fillable cell");
        }

        ((FillableCell) cell).setPipe(null);
        filledTiles.remove(coord);
    }

    public void fillTiles(int distance) {
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;

        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(sourceCell.coord);
        filledTiles.add(sourceCell.coord);

        while (!queue.isEmpty() && distance >= 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Coordinate current = queue.poll();
                Cell cell = cells[current.row][current.col];

                if (cell instanceof FillableCell) {
                    Pipe pipe = ((FillableCell) cell).getPipe();
                    if (pipe != null) {
                        pipe.setFilled();
                        for (Direction dir : pipe.getConnections()) {
                            Coordinate neighbor = current.add(dir.getOffset());
                            if (neighbor.row >= 0 && neighbor.row < rows && 
                                neighbor.col >= 0 && neighbor.col < cols && 
                                !filledTiles.contains(neighbor)) {
                                filledTiles.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
            distance--;
        }
    }

    public boolean checkPath() {
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (current.equals(sinkCell.coord)) {
                return true;
            }

            Cell cell = cells[current.row][current.col];
            if (cell instanceof FillableCell) {
                Pipe pipe = ((FillableCell) cell).getPipe();
                if (pipe != null && pipe.getFilled()) {
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate neighbor = current.add(dir.getOffset());
                        if (neighbor.row >= 0 && neighbor.row < rows && 
                            neighbor.col >= 0 && neighbor.col < cols && 
                            !visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledDistance != null && filledTiles.size() == prevFilledTiles;
    }

    // Rest of the existing methods remain unchanged...
}