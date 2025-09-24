
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
                if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                    cells[i][j] = new Wall(new Coordinate(i, j));
                } else {
                    cells[i][j] = new FillableCell(new Coordinate(i, j));
                }
            }
        }
        
        // Generate source and sink
        var sourceInfo = generateSourceCellInfo();
        var sinkInfo = generateSinkCellInfo();
        
        sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.dir, TerminationCell.Type.SOURCE);
        sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.dir, TerminationCell.Type.SINK);
        
        cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
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

    @NotNull
    private TerminationCell.CreateInfo generateSourceCellInfo() {
        Random rng = new Random();
        Coordinate coord;
        Direction direction;

        do {
            int row = rng.nextInt(rows - 2) + 1;
            int col = rng.nextInt(cols - 2) + 1;
            coord = new Coordinate(row, col);

            int dir = rng.nextInt(4);
            direction = Direction.values()[dir];

            Coordinate next = coord.add(direction.getOffset());
            if (next.row <= 0 || next.row >= rows - 1 || next.col <= 0 || next.col >= cols - 1) {
                continue;
            }

            break;
        } while (true);

        return new TerminationCell.CreateInfo(coord, direction);
    }

    @NotNull
    private TerminationCell.CreateInfo generateSinkCellInfo() {
        Random rng = new Random();
        Coordinate coord;
        Direction direction;

        do {
            boolean onEdge = rng.nextBoolean();
            if (onEdge) {
                int row = rng.nextInt(2) == 0 ? 0 : rows - 1;
                int col = rng.nextInt(cols - 2) + 1;
                coord = new Coordinate(row, col);
                direction = row == 0 ? Direction.UP : Direction.DOWN;
            } else {
                int col = rng.nextInt(2) == 0 ? 0 : cols - 1;
                int row = rng.nextInt(rows - 2) + 1;
                coord = new Coordinate(row, col);
                direction = col == 0 ? Direction.LEFT : Direction.RIGHT;
            }

            if (!coord.equals(sourceCell.coord)) {
                break;
            }
        } while (true);

        return new TerminationCell.CreateInfo(coord, direction);
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
        if (fillableCell.getPipe().isPresent()) {
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
                        if (fillableCell.getPipe().isPresent() && !filledTiles.contains(neighbor)) {
                            fillableCell.getPipe().get().setFilled();
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
            Direction[] connections;
            
            if (cell instanceof TerminationCell) {
                TerminationCell tc = (TerminationCell) cell;
                connections = new Direction[]{tc.pointingTo};
            } else if (cell instanceof FillableCell) {
                Optional<Pipe> pipe = ((FillableCell) cell).getPipe();
                if (!pipe.isPresent()) continue;
                connections = pipe.get().getConnections();
            } else {
                continue;
            }
            
            for (Direction dir : connections) {
                Coordinate neighbor = current.add(dir.getOffset());
                if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                    continue;
                }
                
                Cell neighborCell = cells[neighbor.row][neighbor.col];
                if (neighborCell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) neighborCell;
                    if (tc.pointingTo.getOpposite() == dir) {
                        queue.add(neighbor);
                    }
                } else if (neighborCell instanceof FillableCell && !visited.contains(neighbor)) {
                    Optional<Pipe> neighborPipe = ((FillableCell) neighborCell).getPipe();
                    if (neighborPipe.isPresent()) {
                        for (Direction neighborDir : neighborPipe.get().getConnections()) {
                            if (neighborDir.getOpposite() == dir) {
                                visited.add(neighbor);
                                queue.add(neighbor);
                                break;
                            }
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