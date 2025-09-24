
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
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map dimensions must be at least 3x3");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        // Fill everything with walls
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.cells[r][c] = new Wall(new Coordinate(r, c));
            }
        }

        // Place exactly one source
        TerminationCell.CreateInfo scInfo = generateStartCellInfo();
        this.sourceCell = TerminationCell.makeSource(scInfo.coord, scInfo.direction);
        cells[scInfo.coord.row][scInfo.coord.col] = sourceCell;

        // Place exactly one sink
        TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
        this.sinkCell = TerminationCell.makeSink(sinkInfo.coord, sinkInfo.direction);
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
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
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map dimensions must be at least 3x3");
        }
        if (cells.length != rows || cells[0].length != cols) {
            throw new IllegalArgumentException("cells array dimensions do not match rows/cols");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        TerminationCell foundSource = null;
        TerminationCell foundSink = null;

        // Copy and validate
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                this.cells[r][c] = cell;
                if (cell instanceof TerminationCell tc) {
                    if (tc.source) {
                        if (foundSource != null) {
                            throw new IllegalArgumentException("More than one source cell found");
                        }
                        // source must be non-edge
                        if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                            throw new IllegalArgumentException("Source cell cannot be on edge");
                        }
                        // must not point into a wall
                        Coordinate next = new Coordinate(r, c).add(tc.direction.getOffset());
                        if (next.row < 0 || next.row >= rows ||
                            next.col < 0 || next.col >= cols ||
                            cells[next.row][next.col] instanceof Wall) {
                            throw new IllegalArgumentException("Source must point into a non-wall cell");
                        }
                        foundSource = tc;
                    } else {
                        if (foundSink != null) {
                            throw new IllegalArgumentException("More than one sink cell found");
                        }
                        // sink must be on edge
                        if (!(r == 0 || r == rows - 1 || c == 0 || c == cols - 1)) {
                            throw new IllegalArgumentException("Sink cell must be on edge");
                        }
                        // must point outside the map
                        Coordinate next = new Coordinate(r, c).add(tc.direction.getOffset());
                        if (next.row >= 0 && next.row < rows &&
                            next.col >= 0 && next.col < cols) {
                            throw new IllegalArgumentException("Sink must point outside the map");
                        }
                        foundSink = tc;
                    }
                }
            }
        }
        if (foundSource == null) {
            throw new IllegalArgumentException("No source cell found");
        }
        if (foundSink == null) {
            throw new IllegalArgumentException("No sink cell found");
        }
        this.sourceCell = foundSource;
        this.sinkCell = foundSink;
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
        var cells = Deserializer.parseString(rows, cols, cellsRep);
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
     * Hint: Remember to check whether the map coordinates are within bounds, and whether the target cell is a 
     * {@link FillableCell}.
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
        Cell target = cells[row][col];
        if (!(target instanceof FillableCell fc)) {
            return false;
        }
        if (fc.pipe.isPresent()) {
            return false;
        }
        fc.pipe(p);
        return true;
    }

    @NotNull
    private TerminationCell.CreateInfo generateStartCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            int r = rng.nextInt(rows), c = rng.nextInt(cols);
            coord = new Coordinate(r, c);
            direction = Direction.values()[rng.nextInt(4)];

            if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                continue;
            }
            switch (direction) {
                case UP    -> { if (r <= 1)     continue; }
                case DOWN  -> { if (r >= rows-2) continue; }
                case LEFT  -> { if (c <= 1)     continue; }
                case RIGHT -> { if (c >= cols-2) continue; }
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
            int r = axisToClamp
                    ? (rng.nextBoolean() ? rows - 1 : 0)
                    : rng.nextInt(rows - 2) + 1;
            int c = !axisToClamp
                    ? (rng.nextBoolean() ? cols - 1 : 0)
                    : rng.nextInt(cols - 2) + 1;

            coord = new Coordinate(r, c);
            if (r == c) continue;

            if (axisToClamp) {
                direction = (r == 0) ? Direction.UP : Direction.DOWN;
            } else {
                direction = (c == 0) ? Direction.LEFT : Direction.RIGHT;
            }
            Coordinate adj = coord.add(direction.getOpposite().getOffset());
            if (adj.equals(sourceCell.coord)) continue;

            break;
        } while (true);

        return new TerminationCell.CreateInfo(coord, direction);
    }

    /**
     * Displays the current map.
     */
    public void display() {
        final int padLength = Integer.valueOf(rows - 1).toString().length();

        Runnable printColumns = () -> {
            System.out.print(StringUtils.createPadding(padLength, ' '));
            System.out.print(' ');
            for (int i = 0; i < cols - 2; ++i) {
                System.out.print((char) ('A' + i));
            }
            System.out.println();
        };

        printColumns.run();
        for (int i = 0; i < rows; ++i) {
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
        if (!(cell instanceof FillableCell fc)) {
            throw new IllegalArgumentException("Cannot undo non-fillable cell");
        }
        fc.clear();
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
        prevFilledTiles   = filledTiles.size();
        prevFilledDistance = distance;

        Queue<Coordinate> q     = new ArrayDeque<>();
        Queue<Integer>    distQ = new ArrayDeque<>();
        q.add(sourceCell.coord);
        distQ.add(0);

        while (!q.isEmpty()) {
            Coordinate curr = q.poll();
            int d = distQ.poll();
            if (d >= distance) continue;
            for (Direction dir : Direction.values()) {
                Coordinate next = curr.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows ||
                    next.col < 0 || next.col >= cols) {
                    continue;
                }
                Cell c = cells[next.row][next.col];
                if (!(c instanceof FillableCell fc)) continue;
                if (filledTiles.contains(next)) continue;
                fc.setFilled();
                filledTiles.add(next);
                q.add(next);
                distQ.add(d + 1);
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
            Coordinate curr = queue.poll();
            for (Direction dir : Direction.values()) {
                Coordinate next = curr.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows ||
                    next.col < 0 || next.col >= cols ||
                    visited.contains(next)) {
                    continue;
                }
                Cell c = cells[next.row][next.col];
                if (c instanceof FillableCell fc && fc.pipe.isPresent()) {
                    Pipe pipe = fc.pipe.get();
                    if (!pipe.isConnected(dir.getOpposite())) {
                        continue;
                    }
                    visited.add(next);
                    queue.add(next);
                } else if (c instanceof TerminationCell tc && tc.sink) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return {@code true} if the game is lost (i.e. no new tiles were filled on last call to fillTiles).
     */
    public boolean hasLost() {
        if (prevFilledDistance == null) {
            return false;
        }
        return filledTiles.size() == prevFilledTiles;
    }
}