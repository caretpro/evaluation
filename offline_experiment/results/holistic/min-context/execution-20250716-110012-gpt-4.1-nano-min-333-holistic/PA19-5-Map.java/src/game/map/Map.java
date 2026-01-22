
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
                if (cell instanceof TerminationCell termCell) {
                    if (termCell.isSource()) {
                        sourceCell = termCell;
                    } else if (termCell.isSink()) {
                        sinkCell = termCell;
                    }
                }
            }
        }
    }

    public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    boolean tryPlacePipe(int row, int col, Pipe p) {
        int r = row - 1;
        int c = col - 1;
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false;
        }
        Cell cell = cells[r][c];
        if (!(cell instanceof FillableCell)) {
            return false;
        }
        if (cell instanceof Pipe) {
            return false;
        }
        cells[r][c] = p;
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

    public void fillBeginTile() {
        sourceCell.setFilled();
        filledTiles.add(sourceCell.coord);
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
    }

    public void fillTiles(int distance) {
        Set<Coordinate> newFilled = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        Map<Coordinate, Integer> distanceMap = new HashMap<>();
        for (Coordinate coord : filledTiles) {
            queue.offer(coord);
            distanceMap.put(coord, 0);
        }
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int currentDist = distanceMap.get(current);
            if (currentDist >= distance) continue;
            for (Coordinate neighbor : current.getNeighbors(rows, cols)) {
                if (!filledTiles.contains(neighbor) && cells[neighbor.row][neighbor.col] instanceof FillableCell) {
                    filledTiles.add(neighbor);
                    newFilled.add(neighbor);
                    distanceMap.put(neighbor, currentDist + 1);
                    queue.offer(neighbor);
                }
            }
        }
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) return false;
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        queue.offer(sourceCell.coord);
        visited.add(sourceCell.coord);
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (current.equals(sinkCell.coord)) return true;
            Cell cell = cells[current.row][current.col];
            if (cell instanceof Pipe pipe) {
                for (Coordinate neighbor : current.getNeighbors(rows, cols)) {
                    if (visited.contains(neighbor)) continue;
                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                    if (neighborCell instanceof Pipe || neighbor.equals(sinkCell.coord)) {
                        if (pipe.connectsTo(current, neighbor)) {
                            visited.add(neighbor);
                            queue.offer(neighbor);
                        }
                    }
                }
            } else if (cell instanceof TerminationCell termCell) {
                for (Coordinate neighbor : current.getNeighbors(rows, cols)) {
                    if (visited.contains(neighbor)) continue;
                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                    if (neighborCell instanceof Pipe pipe) {
                        if (pipe.connectsTo(current, neighbor)) {
                            visited.add(neighbor);
                            queue.offer(neighbor);
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
            if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
                break;
            }
        } while (true);
        return new TerminationCell.CreateInfo(coord, direction);
    }

    private TerminationCell.CreateInfo generateEndCellInfo() {
        Random rng = new Random();
        Coordinate coord;
        Direction direction;
        do {
            boolean axisToClamp = rng.nextInt(2) == 1;
            int row, col;
            if (axisToClamp) {
                row = rng.nextInt(2) == 1 ? rows - 1 : 0;
                col = rng.nextInt(rows - 2) + 1;
            } else {
                col = rng.nextInt(2) == 1 ? cols - 1 : 0;
                row = rng.nextInt(cols - 2) + 1;
            }
            coord = new Coordinate(row, col);
            if (row == 0) {
                direction = Direction.UP;
            } else if (row == rows - 1) {
                direction = Direction.DOWN;
            } else if (col == 0) {
                direction = Direction.LEFT;
            } else {
                direction = Direction.RIGHT;
            }
            Coordinate adjacent = coord.add(direction.getOpposite().getOffset());
            if (adjacent.equals(sourceCell.coord)) {
                continue;
            }
            break;
        } while (true);
        return new TerminationCell.CreateInfo(coord, direction);
    }
}