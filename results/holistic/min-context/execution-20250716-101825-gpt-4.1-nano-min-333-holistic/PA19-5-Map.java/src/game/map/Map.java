
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
     * Creates a map with size of rows x cols.
     */
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

    /**
     * Creates a map with the given cells.
     */
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

    @NotNull
    static Map fromString(int rows, int cols, @NotNull String cellsRep) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
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
        if (cell instanceof FillableCell) {
            if (cell instanceof Pipe) {
                return false; // Already occupied
            }
            cells[r][c] = p;
            return true;
        }
        return false;
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

            int dirIdx = rng.nextInt(4);
            direction = Direction.values()[dirIdx];

            if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
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

    @NotNull
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
                direction = (row == 0) ? Direction.UP : Direction.DOWN;
            } else {
                direction = (col == 0) ? Direction.LEFT : Direction.RIGHT;
            }

            Coordinate adjacentCell = coord.add(direction.getOpposite().getOffset());
            if (adjacentCell.equals(sourceCell.coord)) continue;

            break;
        } while (true);

        return new TerminationCell.CreateInfo(coord, direction);
    }

    public void display() {
        final int padLength = Integer.toString(rows - 1).length();

        Runnable printColumns = () -> {
            System.out.print(StringUtils.createPadding(padLength, ' '));
            System.out.print(' ');
            for (int i = 0; i < cols - 2; ++i) {
                System.out.print((char) ('A' + i));
            }
            System.out.println();
        };

        printColumns.run();

        for (int i = 0; i < rows; i++) {
            if (i != 0 && i != rows - 1) {
                System.out.print(String.format("%1$" + padLength + "s", i));
            } else {
                System.out.print(StringUtils.createPadding(padLength, ' '));
            }

            for (Cell cell : cells[i]) {
                System.out.print(cell.toSingleChar());
            }

            if (i != 0 && i != rows - 1) {
                System.out.print(i);
            }
            System.out.println();
        }
        printColumns.run();
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
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
    }

    public void fillTiles(int distance) {
        if (distance < 0) return;
        Set<Coordinate> newFilled = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        for (Coordinate coord : filledTiles) {
            queue.offer(coord);
        }
        Set<Coordinate> visited = new HashSet<>(filledTiles);
        int currentDistance = 0;

        while (!queue.isEmpty() && currentDistance <= distance) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Coordinate current = queue.poll();
                newFilled.add(current);
                for (Direction dir : Direction.values()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                        if (!visited.contains(neighbor)) {
                            Cell cell = cells[neighbor.row][neighbor.col];
                            if (cell instanceof FillableCell) {
                                visited.add(neighbor);
                                queue.offer(neighbor);
                            }
                        }
                    }
                }
            }
            currentDistance++;
        }
        filledTiles.addAll(newFilled);
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) {
            return false;
        }
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        queue.offer(sourceCell.coord);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (current.equals(sinkCell.coord)) {
                return true;
            }
            Cell cell = cells[current.row][current.col];
            if (cell instanceof Pipe) {
                Pipe pipe = (Pipe) cell;
                for (Direction dir : pipe.getConnectedDirections()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                        if (!visited.contains(neighbor)) {
                            Cell neighborCell = cells[neighbor.row][neighbor.col];
                            if (neighborCell instanceof Pipe neighborPipe) {
                                if (neighborPipe.isConnected(dir.getOpposite())) {
                                    visited.add(neighbor);
                                    queue.offer(neighbor);
                                }
                            } else if (neighbor.equals(sinkCell.coord)) {
                                return true;
                            }
                        }
                    }
                }
            } else if (cell instanceof TerminationCell) {
                TerminationCell termCell = (TerminationCell) cell;
                if (current.equals(termCell.coord)) {
                    if (termCell.isSource()) {
                        for (Direction dir : Direction.values()) {
                            if (termCell.getDirection() == dir) {
                                Coordinate neighbor = current.add(dir.getOffset());
                                if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                                    if (neighborCell instanceof Pipe pipe && pipe.isConnected(dir.getOpposite())) {
                                        if (!visited.contains(neighbor)) {
                                            visited.add(neighbor);
                                            queue.offer(neighbor);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (termCell.isSink()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == filledTiles.size();
    }
}