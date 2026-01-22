
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
     * Constructs a map from a map string.
     * <p>This is a convenience method for unit testing.</p>
     *
     * @param rows     Number of rows.
     * @param cols     Number of columns.
     * @param cellsRep String representation of the map, with lines delimited by {@code '\n'}.
     * @return A map with the cells set from {@code cellsRep}.
     * @throws IllegalArgumentException If the map is incorrectly formatted.
     */
    @NotNull
    public static Map fromString(int rows, int cols, @NotNull String cellsRep) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    /**
     * Initializes a completely empty map of size rows×cols,
     * with interior FillableCells and border Walls, plus one source and one sink.
     *
     * @param rows Number of rows.
     * @param cols Number of columns.
     */
    public Map(int rows, int cols) {
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map must be at least 3x3");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        // fill interior vs. border
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    cells[r][c] = new Wall();
                } else {
                    cells[r][c] = new FillableCell();
                }
            }
        }

        var startInfo = generateStartCellInfo();
        this.sourceCell = TerminationCell.createSource(startInfo.coord, startInfo.direction);
        cells[startInfo.coord.row][startInfo.coord.col] = sourceCell;

        var endInfo = generateEndCellInfo();
        this.sinkCell = TerminationCell.createSink(endInfo.coord, endInfo.direction);
        cells[endInfo.coord.row][endInfo.coord.col] = sinkCell;
    }

    /**
     * Initializes a map with the given cells.
     * <p>The map should only contain one source tile in any non-edge cell and one sink tile on the edge.
     * The source must not point into a wall; the sink must point outside the map.</p>
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param cells Cells to fill the map.
     */
    public Map(int rows, int cols, Cell[][] cells) {
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map must be at least 3x3");
        }
        if (cells.length != rows) {
            throw new IllegalArgumentException("cells.length != rows");
        }
        for (int r = 0; r < rows; r++) {
            if (cells[r].length != cols) {
                throw new IllegalArgumentException("cells[" + r + "].length != cols");
            }
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        TerminationCell foundSource = null;
        TerminationCell foundSink = null;

        // validate exactly one source and one sink, with correct placement
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell tc) {
                    if (tc.isSource()) {
                        if (foundSource != null) {
                            throw new IllegalArgumentException("Multiple source tiles found");
                        }
                        if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                            throw new IllegalArgumentException("Source tile must not be on edge");
                        }
                        var adj = new Coordinate(r, c).add(tc.getDirection().getOffset());
                        if (adj.row < 0 || adj.row >= rows || adj.col < 0 || adj.col >= cols
                                || cells[adj.row][adj.col] instanceof Wall) {
                            throw new IllegalArgumentException("Source points into wall or outside map");
                        }
                        foundSource = tc;
                    } else {
                        if (foundSink != null) {
                            throw new IllegalArgumentException("Multiple sink tiles found");
                        }
                        if (r != 0 && r != rows - 1 && c != 0 && c != cols - 1) {
                            throw new IllegalArgumentException("Sink tile must be on edge");
                        }
                        var adj = new Coordinate(r, c).add(tc.getDirection().getOffset());
                        if (adj.row >= 0 && adj.row < rows && adj.col >= 0 && adj.col < cols) {
                            throw new IllegalArgumentException("Sink points into map");
                        }
                        foundSink = tc;
                    }
                    this.cells[r][c] = tc;
                } else {
                    this.cells[r][c] = cell;
                }
            }
        }
        if (foundSource == null) {
            throw new IllegalArgumentException("No source tile found");
        }
        if (foundSink == null) {
            throw new IllegalArgumentException("No sink tile found");
        }
        this.sourceCell = foundSource;
        this.sinkCell = foundSink;
    }

    /**
     * Tries to place a pipe at (row, col).
     *
     * @param coord Coordinate to place pipe at.
     * @param pipe  Pipe to place in cell.
     * @return {@code true} if the pipe was placed, {@code false} otherwise.
     */
    public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    private boolean tryPlacePipe(int row, int col, Pipe pipe) {
        int r = row - 1, c = col - 1;
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false;
        }
        Cell cell = cells[r][c];
        if (!(cell instanceof FillableCell fillable)) {
            return false;
        }
        if (fillable.hasPipe()) {
            return false;
        }
        fillable.setPipe(pipe);
        return true;
    }

    /**
     * Undoes the last placement at {@code coord}.
     *
     * @param coord Coordinate to reset.
     * @throws IllegalArgumentException if the cell is not a FillableCell.
     */
    public void undo(final Coordinate coord) {
        Cell cell = cells[coord.row][coord.col];
        if (!(cell instanceof FillableCell fillable)) {
            throw new IllegalArgumentException("Cannot undo at non-fillable cell: " + coord);
        }
        fillable.clearPipe();
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = null;
        filledTiles.clear();
    }

    /**
     * Fills all pipes that are within {@code distance} units from the sourceCell.
     *
     * @param distance Distance to fill pipes.
     */
    public void fillTiles(int distance) {
        if (!Objects.equals(prevFilledDistance, distance)) {
            filledTiles.clear();
            prevFilledTiles = 0;
            prevFilledDistance = distance;
        }
        Queue<Coordinate> queue = new ArrayDeque<>();
        Map<Coordinate, Integer> depth = new HashMap<>();
        Coordinate start = sourceCell.getCoord();
        queue.add(start);
        depth.put(start, 0);
        sourceCell.setFilled();
        filledTiles.add(start);

        while (!queue.isEmpty()) {
            Coordinate coord = queue.poll();
            int d = depth.get(coord);
            if (d >= distance) {
                continue;
            }
            for (Direction dir : Direction.values()) {
                Coordinate next = coord.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                Cell c = cells[next.row][next.col];
                if (c instanceof Wall) {
                    continue;
                }
                if (depth.containsKey(next) && depth.get(next) <= d + 1) {
                    continue;
                }
                boolean traversable = false;
                if (c instanceof FillableCell fc) {
                    traversable = fc.hasPipe();
                } else if (c instanceof TerminationCell tc && !tc.isSource()) {
                    traversable = true;
                }
                if (!traversable) {
                    continue;
                }
                depth.put(next, d + 1);
                queue.add(next);
                if (c instanceof FillableCell fc2) {
                    fc2.setFilled();
                } else {
                    ((TerminationCell) c).setFilled();
                }
                filledTiles.add(next);
            }
        }
        prevFilledTiles = filledTiles.size();
    }

    /**
     * Hint: The game is lost when a round ends and no pipes are filled during the round.
     *
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        return filledTiles.size() == prevFilledTiles;
    }

    /**
     * Checks whether there exists a path from source to sink along filled pipes.
     *
     * @return {@code true} if a path exists.
     */
    public boolean checkPath() {
        Queue<Coordinate> queue = new ArrayDeque<>();
        Set<Coordinate> visited = new HashSet<>();
        Coordinate start = sourceCell.getCoord();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            if (cur.equals(sinkCell.getCoord())) {
                return true;
            }
            for (Direction dir : Direction.values()) {
                Coordinate next = cur.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (!visited.add(next)) {
                    continue;
                }
                Cell c = cells[next.row][next.col];
                boolean ok =
                    (c instanceof FillableCell fc && fc.isFilled()) ||
                    (c instanceof TerminationCell tc && !tc.isSource() && tc.isFilled());
                if (ok) {
                    queue.add(next);
                }
            }
        }
        return false;
    }

    /**
     * Displays the current map.
     */
    public void display() {
        int pad = Integer.toString(rows - 1).length();
        Runnable colHeaders = () -> {
            System.out.print(StringUtils.createPadding(pad, ' '));
            System.out.print(' ');
            for (int i = 0; i < cols - 2; i++) {
                System.out.print((char) ('A' + i));
            }
            System.out.println();
        };
        colHeaders.run();

        for (int r = 0; r < rows; r++) {
            if (r != 0 && r != rows - 1) {
                System.out.printf("%" + pad + "d", r);
            } else {
                System.out.print(StringUtils.createPadding(pad, ' '));
            }
            for (Cell c : cells[r]) {
                System.out.print(c.toSingleChar());
            }
            if (r != 0 && r != rows - 1) {
                System.out.print(r);
            }
            System.out.println();
        }
        colHeaders.run();
    }

    // ——— Private helpers to pick random source and sink positions ———

    @NotNull
    private TerminationCell.CreateInfo generateStartCellInfo() {
        Random rng = new Random();
        while (true) {
            int r = rng.nextInt(rows - 2) + 1;
            int c = rng.nextInt(cols - 2) + 1;
            var dir = Direction.values()[rng.nextInt(4)];
            if (cells[r][c] instanceof Wall) {
                continue;
            }
            var adj = new Coordinate(r, c).add(dir.getOffset());
            if (adj.row < 0 || adj.row >= rows || adj.col < 0 || adj.col >= cols) {
                continue;
            }
            if (cells[adj.row][adj.col] instanceof Wall) {
                continue;
            }
            return new TerminationCell.CreateInfo(new Coordinate(r, c), dir);
        }
    }

    @NotNull
    private TerminationCell.CreateInfo generateEndCellInfo() {
        Random rng = new Random();
        while (true) {
            boolean onRow = rng.nextBoolean();
            int r = onRow
                  ? (rng.nextBoolean() ? 0 : rows - 1)
                  : rng.nextInt(rows - 2) + 1;
            int c = onRow
                  ? rng.nextInt(cols - 2) + 1
                  : (rng.nextBoolean() ? 0 : cols - 1);
            var dir = onRow
                    ? (r == 0 ? Direction.UP : Direction.DOWN)
                    : (c == 0 ? Direction.LEFT : Direction.RIGHT);
            var coord = new Coordinate(r, c);
            if (coord.equals(sourceCell.getCoord())) {
                continue;
            }
            return new TerminationCell.CreateInfo(coord, dir);
        }
    }
}