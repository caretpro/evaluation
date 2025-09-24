
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
        var sinkInfo = generateEndCellInfo();
        
        sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.direction, TerminationCell.Type.SOURCE);
        sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.direction, TerminationCell.Type.SINK);
        
        cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        
        // Validate cells
        int sourceCount = 0;
        int sinkCount = 0;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = cells[i][j];
                if (cell instanceof TerminationCell) {
                    TerminationCell tc = (TerminationCell) cell;
                    if (tc.type == TerminationCell.Type.SOURCE) {
                        sourceCell = tc;
                        sourceCount++;
                    } else {
                        sinkCell = tc;
                        sinkCount++;
                    }
                }
            }
        }
        
        if (sourceCount != 1 || sinkCount != 1) {
            throw new IllegalArgumentException("Map must contain exactly one source and one sink");
        }
    }

    boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 1 || row > rows || col < 1 || col > cols) {
            return false;
        }
        
        int actualRow = row - 1;
        int actualCol = col - 1;
        
        Cell cell = cells[actualRow][actualCol];
        if (!(cell instanceof FillableCell)) {
            return false;
        }
        
        FillableCell fillableCell = (FillableCell) cell;
        if (fillableCell.pipe != null) {
            return false;
        }
        
        fillableCell.pipe = p;
        return true;
    }

    public void undo(final Coordinate coord) {
        Cell cell = cells[coord.row][coord.col];
        if (!(cell instanceof FillableCell)) {
            throw new IllegalArgumentException("Cannot undo non-fillable cell");
        }
        
        FillableCell fillableCell = (FillableCell) cell;
        fillableCell.pipe = null;
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
                for (Direction dir : Direction.values()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (isValidCoordinate(neighbor)) {
                        Cell cell = cells[neighbor.row][neighbor.col];
                        if (cell instanceof FillableCell && !filledTiles.contains(neighbor)) {
                            FillableCell fillableCell = (FillableCell) cell;
                            if (fillableCell.pipe != null && fillableCell.pipe.canConnect(dir.getOpposite())) {
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
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);
        
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (current.equals(sinkCell.coord)) {
                return true;
            }
            
            for (Direction dir : Direction.values()) {
                Coordinate neighbor = current.add(dir.getOffset());
                if (isValidCoordinate(neighbor)) {
                    Cell cell = cells[neighbor.row][neighbor.col];
                    if (cell instanceof TerminationCell && neighbor.equals(sinkCell.coord)) {
                        return true;
                    }
                    if (cell instanceof FillableCell && !visited.contains(neighbor)) {
                        FillableCell fillableCell = (FillableCell) cell;
                        if (fillableCell.pipe != null && fillableCell.pipe.canConnect(dir.getOpposite())) {
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

    @NotNull
    private TerminationCell.CreateInfo generateStartCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            int row = rng.nextInt(rows);
            int col = rng.nextInt(cols);
            coord = new Coordinate(row, col);

            int dir = rng.nextInt(4);
            direction = Direction.values()[dir];

            if (row == 0 || row == rows - 1) {
                continue;
            }
            if (col == 0 || col == cols - 1) {
                continue;
            }
            switch (direction) {
            case UP:
                if (row <= 1) {
                    continue;
                }
                break;
            case DOWN:
                if (row >= rows - 2) {
                    continue;
                }
                break;
            case LEFT:
                if (col <= 1) {
                    continue;
                }
                break;
            case RIGHT:
                if (col >= cols - 2) {
                    continue;
                }
                break;
            }

            break;
        } while (true);

        return new TerminationCell.CreateInfo(coord, direction);
    }

    @NotNull
    private TerminationCell.CreateInfo generateEndCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            boolean axisToClamp = rng.nextInt(2) == 1;
            int row = axisToClamp ? (rng.nextInt(2) == 1 ? rows - 1 : 0) : rng.nextInt(rows - 2) + 1;
            int col = !axisToClamp ? (rng.nextInt(2) == 1 ? cols - 1 : 0) : rng.nextInt(cols - 2) + 1;

            if (row == col) {
                continue;
            }

            coord = new Coordinate(row, col);

            if (axisToClamp) {
                if (row == 0) {
                    direction = Direction.UP;
                } else {
                    direction = Direction.DOWN;
                }
            } else {
                if (col == 0) {
                    direction = Direction.LEFT;
                } else {
                    direction = Direction.RIGHT;
                }
            }

            var adjacentCell = coord.add(direction.getOpposite().getOffset());
            if (adjacentCell.equals(sourceCell.coord)) {
                continue;
            }

            break;
        } while (true);

        return new TerminationCell.CreateInfo(coord, direction);
    }

    // ... [rest of the existing methods remain unchanged] ...
}