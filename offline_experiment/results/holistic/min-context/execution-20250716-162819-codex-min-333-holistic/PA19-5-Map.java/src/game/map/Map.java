
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
     *
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

        // fill all as walls initially
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Wall(new Coordinate(r, c));
            }
        }

        // randomly pick source and sink cells
        TerminationCell.CreateInfo startInfo = generateStartCellInfo();
        this.sourceCell = TerminationCell.createSource(startInfo.coord, startInfo.direction);
        cells[startInfo.coord.row][startInfo.coord.col] = sourceCell;

        TerminationCell.CreateInfo endInfo = generateEndCellInfo();
        this.sinkCell = TerminationCell.createSink(endInfo.coord, endInfo.direction);
        cells[endInfo.coord.row][endInfo.coord.col] = sinkCell;

        // fill the rest with empty fillable cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!(cells[r][c] instanceof Wall)
                        && (r != startInfo.coord.row || c != startInfo.coord.col)
                        && (r != endInfo.coord.row   || c != endInfo.coord.col)) {
                    cells[r][c] = new FillableCell(new Coordinate(r, c));
                }
            }
        }
    }

    /**
     * Creates a map with the given cells.
     *
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
        if (cells.length != rows ||
            Arrays.stream(cells).anyMatch(row -> row.length != cols)) {
            throw new IllegalArgumentException("Dimensions mismatch");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        TerminationCell foundSource = null;
        TerminationCell foundSink   = null;
        int countSource = 0, countSink = 0;

        // validate and copy
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                this.cells[r][c] = cell;
                if (cell instanceof TerminationCell tc) {
                    if (tc.isSource()) {
                        foundSource = tc;
                        countSource++;
                    } else {
                        foundSink = tc;
                        countSink++;
                    }
                }
            }
        }
        if (countSource != 1 || countSink != 1) {
            throw new IllegalArgumentException("Must have exactly one source and one sink");
        }
        this.sourceCell = foundSource;
        this.sinkCell   = foundSink;
    }

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
        Cell[][] cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    /**
     * Tries to place a pipe at (row, col).
     *
     * @param coord Coordinate to place pipe at.
     * @param pipe  Pipe to place in cell.
     * @return {@code true} if the pipe is placed in the cell, {@code false} otherwise.
     */
    public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Tries to place a pipe at (row, col).
     *
     * <p>
     * Note: You cannot overwrite the pipe of a cell once the cell is occupied.
     * </p>
     * <p>
     * Hint: Remember to check whether the map coordinates are within bounds, and whether the target cell is a {@link FillableCell}.
     * </p>
     *
     * @param row One-Based row number to place pipe at.
     * @param col One-Based column number to place pipe at.
     * @param p   Pipe to place in cell.
     * @return {@code true} if the pipe is placed in the cell, {@code false} otherwise.
     */
    boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        Cell cell = cells[row][col];
        if (cell instanceof FillableCell fc && !fc.hasPipe()) {
            fc.placePipe(p);
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
            direction = Direction.values()[rng.nextInt(Direction.values().length)];

            if (row == 0 || row == rows - 1) continue;
            if (col == 0 || col == cols - 1) continue;
            switch (direction) {
                case UP    -> { if (row <= 1)     continue; }
                case DOWN  -> { if (row >= rows-2) continue; }
                case LEFT  -> { if (col <= 1)     continue; }
                case RIGHT -> { if (col >= cols-2) continue; }
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
            boolean axisToClamp = rng.nextBoolean();
            int row = axisToClamp
                      ? (rng.nextBoolean() ? rows - 1 : 0)
                      : rng.nextInt(rows - 2) + 1;
            int col = !axisToClamp
                      ? (rng.nextBoolean() ? cols - 1 : 0)
                      : rng.nextInt(cols - 2) + 1;

            coord = new Coordinate(row, col);

            if (axisToClamp) {
                direction = (row == 0) ? Direction.UP : Direction.DOWN;
            } else {
                direction = (col == 0) ? Direction.LEFT : Direction.RIGHT;
            }

            Coordinate adjacent = coord.add(direction.getOpposite().getOffset());
            if (adjacent.equals(sourceCell.coord)) {
                continue;
            }
            break;
        } while (true);

        return new TerminationCell.CreateInfo(coord, direction);
    }

    /**
     * Displays the current map.
     */
    public void display() {
        final int padLength = Integer.toString(rows - 1).length();
        Runnable printColumns = () -> {
            System.out.print(StringUtils.createPadding(padLength, ' '));
            System.out.print(' ');
            for (int i = 0; i < cols - 2; i++) {
                System.out.print((char) ('A' + i));
            }
            System.out.println();
        };
        printColumns.run();
        for (int i = 0; i < rows; i++) {
            if (i != 0 && i != rows - 1) {
                System.out.print(String.format("%" + padLength + "d", i));
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

    /**
     * Undoes a step from the map.
     *
     * <p>
     * Effectively replaces the cell with an empty cell in the coordinate specified.
     * </p>
     *
     * @param coord Coordinate to reset.
     * @throws IllegalArgumentException if the cell is not an instance of {@link FillableCell}.
     */
    public void undo(final Coordinate coord) {
        Cell cell = cells[coord.row][coord.col];
        if (!(cell instanceof FillableCell)) {
            throw new IllegalArgumentException("Can only undo on fillable cells");
        }
        FillableCell fc = (FillableCell) cell;
        fc.clearPipe();
        filledTiles.remove(coord);
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
    }

    /**
     * Fills all pipes that are within {@code distance} units from the {@code sourceCell}.
     *
     * @param distance Distance to fill pipes.
     */
    public void fillTiles(int distance) {
        prevFilledDistance = distance;
        prevFilledTiles = filledTiles.size();

        Queue<Coordinate> queue = new ArrayDeque<>();
        queue.add(sourceCell.coord);
        Map<Coordinate, Integer> distMap = new HashMap<>();
        distMap.put(sourceCell.coord, 0);
        sourceCell.setFilled();
        filledTiles.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            int d = distMap.get(cur);
            if (d >= distance) {
                continue;
            }
            for (Direction dir : Direction.values()) {
                Coordinate adj = cur.add(dir.getOffset());
                if (adj.row < 0 || adj.row >= rows || adj.col < 0 || adj.col >= cols) {
                    continue;
                }
                Cell neighbor = cells[adj.row][adj.col];
                if (neighbor instanceof FillableCell fc && fc.hasPipe() && !filledTiles.contains(adj)) {
                    fc.setFilled();
                    filledTiles.add(adj);
                    distMap.put(adj, d + 1);
                    queue.add(adj);
                } else if (neighbor instanceof TerminationCell tc && !filledTiles.contains(adj)) {
                    tc.setFilled();
                    filledTiles.add(adj);
                }
            }
        }
    }

    /**
     * Checks whether there exists a path from {@code sourceCell} to {@code sinkCell}.
     *
     * @return {@code true} if a path exists, else {@code false}.
     */
    public boolean checkPath() {
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> queue = new ArrayDeque<>();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            if (cur.equals(sinkCell.coord)) {
                return true;
            }
            for (Direction dir : Direction.values()) {
                Coordinate adj = cur.add(dir.getOffset());
                if (adj.row < 0 || adj.row >= rows || adj.col < 0 || adj.col >= cols) {
                    continue;
                }
                Cell neighbor = cells[adj.row][adj.col];
                if ((neighbor instanceof FillableCell fc && fc.isFilled())
                        || (neighbor instanceof TerminationCell tc && tc.isFilled())) {
                    if (visited.add(adj)) {
                        queue.add(adj);
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        if (prevFilledDistance == null) {
            return false;
        }
        return prevFilledTiles == filledTiles.size();
    }
}