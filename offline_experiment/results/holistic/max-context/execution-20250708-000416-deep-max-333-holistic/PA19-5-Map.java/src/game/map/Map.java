
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
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map must be at least 3x3");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        // Initialize all cells as empty first
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new FillableCell(new Coordinate(i, j));
            }
        }

        // Generate source and sink cells
        var sourceInfo = generateStartCellInfo();
        var sinkInfo = generateEndCellInfo();

        sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.dir, TerminationCell.Type.SOURCE);
        sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.dir, TerminationCell.Type.SINK);

        cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map must be at least 3x3");
        }
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
            throw new IllegalArgumentException("Map must contain exactly one source and one sink");
        }
    }

    boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 1 || row > rows || col < 1 || col > cols) {
            return false;
        }

        int actualRow = row - 1;
        int actualCol = col - 1;

        Cell target = cells[actualRow][actualCol];
        if (!(target instanceof FillableCell)) {
            return false;
        }

        FillableCell fillableCell = (FillableCell) target;
        if (fillableCell.getPipe() != null) {
            return false;
        }

        fillableCell.setPipe(p);
        return true;
    }

    public void undo(final Coordinate coord) {
        Cell cell = cells[coord.row][coord.col];
        if (!(cell instanceof FillableCell)) {
            throw new IllegalArgumentException("Can only undo fillable cells");
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
                            if (isValidCoordinate(neighbor) && !filledTiles.contains(neighbor)) {
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
                if (pipe != null) {
                    for (Direction dir : pipe.getConnections()) {
                        Coordinate neighbor = current.add(dir.getOffset());
                        if (isValidCoordinate(neighbor) && !visited.contains(neighbor)) {
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

    private boolean isValidCoordinate(Coordinate coord) {
        return coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols;
    }

    // Rest of the existing methods remain unchanged...
}