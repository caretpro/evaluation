
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
        
        // Initialize all cells as empty fillable cells first
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new FillableCell(new Coordinate(i, j));
            }
        }
        
        // Generate source and sink cells
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
        if (row < 1 || row > rows || col < 1 || col > cols) {
            return false;
        }
        
        int actualRow = row - 1;
        int actualCol = col - 1;
        
        Cell targetCell = cells[actualRow][actualCol];
        if (!(targetCell instanceof FillableCell)) {
            return false;
        }
        
        FillableCell fillableCell = (FillableCell) targetCell;
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
        
        FillableCell fillableCell = (FillableCell) cell;
        fillableCell.setPipe(null);
        filledTiles.remove(coord);
    }

    public void fillTiles(int distance) {
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;
        
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(sourceCell.coord);
        filledTiles.add(sourceCell.coord);
        
        while (!queue.isEmpty() && distance >= 0) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Coordinate current = queue.poll();
                
                for (Direction dir : Direction.values()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                        continue;
                    }
                    
                    Cell cell = cells[neighbor.row][neighbor.col];
                    if (cell instanceof FillableCell) {
                        FillableCell fillableCell = (FillableCell) cell;
                        Pipe pipe = fillableCell.getPipe();
                        if (pipe != null && !filledTiles.contains(neighbor)) {
                            filledTiles.add(neighbor);
                            queue.add(neighbor);
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
            Direction[] connections = getConnections(cell);
            
            for (Direction dir : connections) {
                Coordinate neighbor = current.add(dir.getOffset());
                if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                    continue;
                }
                
                if (!visited.contains(neighbor)) {
                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                    if (isConnected(neighborCell, dir.getOpposite())) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledDistance != null && filledTiles.size() == prevFilledTiles;
    }

    private Direction[] getConnections(Cell cell) {
        if (cell instanceof TerminationCell) {
            TerminationCell tc = (TerminationCell) cell;
            return new Direction[]{tc.pointingTo};
        } else if (cell instanceof FillableCell) {
            Pipe pipe = ((FillableCell) cell).getPipe();
            if (pipe != null) {
                return pipe.getConnections();
            }
        }
        return new Direction[0];
    }

    private boolean isConnected(Cell cell, Direction fromDirection) {
        if (cell instanceof TerminationCell) {
            TerminationCell tc = (TerminationCell) cell;
            return tc.pointingTo.getOpposite() == fromDirection;
        } else if (cell instanceof FillableCell) {
            Pipe pipe = ((FillableCell) cell).getPipe();
            if (pipe != null) {
                for (Direction dir : pipe.getConnections()) {
                    if (dir == fromDirection) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Rest of the existing methods remain unchanged...
}