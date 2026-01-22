
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

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new FillableCell(new Coordinate(r, c));
            }
        }

        TerminationCell.CreateInfo startInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(startInfo.coord, startInfo.direction);
        cells[startInfo.coord.row][startInfo.coord.col] = sourceCell;

        TerminationCell.CreateInfo endInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(endInfo.coord, endInfo.direction);
        cells[endInfo.coord.row][endInfo.coord.col] = sinkCell;
    }

    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        for (int r = 0; r < rows; r++) {
            System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = this.cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell termCell = (TerminationCell) cell;
                    if (termCell.isSource()) {
                        sourceCell = termCell;
                    } else if (termCell.isSink()) {
                        sinkCell = termCell;
                    }
                }
            }
        }
        if (sourceCell == null || sinkCell == null) {
            throw new IllegalArgumentException("Map must contain one source and one sink cell");
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
        if (cell instanceof Pipe) {
            return false;
        }
        cells[row][col] = p; // Place the pipe
        return true;
    }

    public void undo(final Coordinate coord) {
        int r = coord.row;
        int c = coord.col;
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            throw new IllegalArgumentException("Coordinate out of bounds");
        }
        Cell cell = cells[r][c];
        if (!(cell instanceof FillableCell)) {
            throw new IllegalArgumentException("Cell is not fillable");
        }
        cells[r][c] = new FillableCell(new Coordinate(r, c));
        filledTiles.remove(coord);
    }

    public void fillTiles(int distance) {
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);
        filledTiles.clear();

        Map<Coordinate, Integer> distMap = new HashMap<>();
        distMap.put(sourceCell.coord, 0);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int currDist = distMap.get(current);
            if (currDist > distance) {
                continue;
            }
            filledTiles.add(current);
            if (currDist == distance) {
                continue;
            }
            for (Direction dir : Direction.values()) {
                Coordinate neighbor = current.add(dir.getOffset());
                if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                    continue;
                }
                if (visited.contains(neighbor)) {
                    continue;
                }
                Cell neighborCell = cells[neighbor.row][neighbor.col];
                if (neighborCell instanceof Wall) {
                    continue;
                }
                visited.add(neighbor);
                distMap.put(neighbor, currDist + 1);
                queue.add(neighbor);
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
            if (current.equals(sinkCell.coord)) {
                return true;
            }
            Cell cell = cells[current.row][current.col];
            if (cell instanceof Pipe || cell instanceof TerminationCell) {
                for (Direction dir : Direction.values()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                        continue;
                    }
                    if (visited.contains(neighbor)) {
                        continue;
                    }
                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                    if (neighborCell instanceof Wall) {
                        continue;
                    }
                    if (neighborCell instanceof Pipe) {
                        Pipe pipe = (Pipe) neighborCell;
                        if (pipe.connectsFrom(current, dir)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    } else if (neighborCell instanceof TerminationCell) {
                        TerminationCell term = (TerminationCell) neighborCell;
                        if (term.equals(sinkCell)) {
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
        return prevFilledTiles == filledTiles.size();
    }

    private TerminationCell.CreateInfo generateStartCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            int row = rng.nextInt(rows);
            int col = rng.nextInt(cols);
            coord = new Coordinate(row, col);

            int dirIdx = rng.nextInt(4);
            direction = Direction.values()[dirIdx];

            if (row == 0 || row == rows - 1) {
                continue;
            }
            if (col == 0 || col == cols - 1) {
                continue;
            }
            switch (direction) {
                case UP:
                    if (row <= 1) continue;
                    break;
                case DOWN:
                    if (row >= rows - 2) continue;
                    break;
                case LEFT:
                    if (col <= 1) continue;
                    break;
                case RIGHT:
                    if (col >= cols - 2) continue;
                    break;
            }
            break;
        } while (true);
        return new TerminationCell.CreateInfo(coord, direction);
    }

    private TerminationCell.CreateInfo generateEndCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            boolean axisToClamp = rng.nextInt(2) == 1;
            int row = axisToClamp ? (rng.nextInt(2) == 1 ? rows - 1 : 0) : rng.nextInt(rows - 2) + 1;
            int col = !axisToClamp ? (rng.nextInt(2) == 1 ? cols - 1 : 0) : rng.nextInt(cols - 2) + 1;

            if (row == col) continue;

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

            Coordinate adjacentCell = coord.add(direction.getOpposite().getOffset());
            if (adjacentCell.equals(sourceCell.coord)) {
                continue;
            }
            break;
        } while (true);
        return new TerminationCell.CreateInfo(coord, direction);
    }
}