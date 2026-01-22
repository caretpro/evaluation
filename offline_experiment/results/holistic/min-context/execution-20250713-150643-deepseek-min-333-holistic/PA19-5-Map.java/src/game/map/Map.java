
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

    public Map(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        
        // Initialize all cells as empty fillable cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cells[i][j] = new FillableCell(i, j);
            }
        }
        
        // Generate source and sink cells
        var sourceInfo = generateStartCellInfo();
        var sinkInfo = generateEndCellInfo();
        
        sourceCell = new TerminationCell(sourceInfo.coord.row, sourceInfo.coord.col, sourceInfo.direction, TerminationCell.Type.SOURCE);
        sinkCell = new TerminationCell(sinkInfo.coord.row, sinkInfo.coord.col, sinkInfo.direction, TerminationCell.Type.SINK);
        
        cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        
        // Find source and sink cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cells[i][j] instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cells[i][j];
                    if (tc.getType() == TerminationCell.Type.SOURCE) {
                        sourceCell = tc;
                    } else if (tc.getType() == TerminationCell.Type.SINK) {
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
        if (prevFilledDistance != null && distance <= prevFilledDistance) {
            return;
        }
        
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;
        
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(sourceCell.coord);
        filledTiles.add(sourceCell.coord);
        
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell cell = cells[current.row][current.col];
            
            if (cell instanceof FillableCell) {
                FillableCell fillableCell = (FillableCell) cell;
                if (fillableCell.getPipe() == null) {
                    continue;
                }
                
                for (Direction dir : fillableCell.getPipe().getPossibleDirections()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                        continue;
                    }
                    
                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                    if (neighborCell instanceof FillableCell) {
                        FillableCell neighborFillable = (FillableCell) neighborCell;
                        if (neighborFillable.getPipe() != null && !filledTiles.contains(neighbor)) {
                            filledTiles.add(neighbor);
                            queue.add(neighbor);
                        }
                    } else if (neighborCell instanceof TerminationCell) {
                        TerminationCell termCell = (TerminationCell) neighborCell;
                        if (termCell.getType() == TerminationCell.Type.SINK) {
                            filledTiles.add(neighbor);
                        }
                    }
                }
            }
        }
    }

    public boolean checkPath() {
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);
        
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            Cell cell = cells[current.row][current.col];
            
            if (cell.equals(sinkCell)) {
                return true;
            }
            
            if (cell instanceof FillableCell) {
                FillableCell fillableCell = (FillableCell) cell;
                if (fillableCell.getPipe() == null) {
                    continue;
                }
                
                for (Direction dir : fillableCell.getPipe().getPossibleDirections()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                        continue;
                    }
                    
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            } else if (cell instanceof TerminationCell) {
                TerminationCell termCell = (TerminationCell) cell;
                if (termCell.getType() == TerminationCell.Type.SOURCE) {
                    Coordinate neighbor = current.add(termCell.getDirection().getOffset());
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
        
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == filledTiles.size() && !checkPath();
    }

    // Rest of the class remains unchanged...
    @NotNull
    static Map fromString(int rows, int cols, @NotNull String cellsRep) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    @NotNull
    private TerminationCell.CreateInfo generateStartCellInfo() {
        // Existing implementation...
    }

    @NotNull
    private TerminationCell.CreateInfo generateEndCellInfo() {
        // Existing implementation...
    }

    public void display() {
        // Existing implementation...
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
    }
}