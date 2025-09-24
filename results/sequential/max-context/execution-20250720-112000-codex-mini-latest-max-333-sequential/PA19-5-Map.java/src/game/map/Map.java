
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

    /** All coordinates of cells that have been filled so far. */
    @NotNull
    private final Set<Coordinate> filledTiles = new HashSet<>();
    /** Number of filled tiles in the previous round (for hasLost logic). */
    private int prevFilledTiles = 0;
    /** Previous distance passed to fillTiles (to reset filledTiles when distance changes). */
    private Integer prevFilledDistance;

    /**
     * Convenience factory for tests.
     *
     * @param rows     Number of rows.
     * @param cols     Number of columns.
     * @param cellsRep Map representation with rows separated by '\n'.
     */
    @NotNull
    static Map fromString(int rows, int cols, @NotNull String cellsRep) {
        var cells = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, cells);
    }

    /**
     * Tries to place a pipe at the given coordinate.
     *
     * @param coord Coordinate to place the pipe.
     * @param pipe  Pipe to place.
     * @return true if placement succeeded; false otherwise.
     */
    public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Tries to place a pipe at (row, col).
     *
     * Note: You cannot overwrite a cell once it's occupied.
     * Hint: Check map bounds and that the target is a FillableCell.
     *
     * @param row One-based row index.
     * @param col One-based column index.
     * @param p   Pipe to place.
     * @return true if the pipe is placed; false otherwise.
     */
    boolean tryPlacePipe(int row, int col, Pipe p) {
        // bounds check
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        Cell c = cells[row][col];
        if (!(c instanceof FillableCell)) {
            return false;
        }
        FillableCell fc = (FillableCell) c;
        // cannot overwrite an existing pipe
        if (fc.getPipe().isPresent()) {
            return false;
        }
        // place the new pipe
        cells[row][col] = new FillableCell(new Coordinate(row, col), p);
        return true;
    }

    @NotNull
    private TerminationCell.CreateInfo generateStartCellInfo() {
        Random rng = new Random();
        Coordinate coord;
        Direction direction;
        do {
            int r = rng.nextInt(rows);
            int c = rng.nextInt(cols);
            coord = new Coordinate(r, c);
            direction = Direction.values()[rng.nextInt(4)];

            // must be strictly interior
            if (r <= 0 || r >= rows - 1 || c <= 0 || c >= cols - 1) {
                continue;
            }
            // also enough space in direction
            switch (direction) {
                case UP    -> { if (r <= 1)        continue; }
                case DOWN  -> { if (r >= rows - 2) continue; }
                case LEFT  -> { if (c <= 1)        continue; }
                case RIGHT -> { if (c >= cols - 2) continue; }
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
            boolean axisClamp = rng.nextBoolean();
            int r = axisClamp
                    ? (rng.nextBoolean() ? rows - 1 : 0)
                    : rng.nextInt(rows - 2) + 1;
            int c = !axisClamp
                    ? (rng.nextBoolean() ? cols - 1 : 0)
                    : rng.nextInt(cols - 2) + 1;
            coord = new Coordinate(r, c);
            if (r == c) {
                continue;
            }
            if (axisClamp) {
                direction = (r == 0) ? Direction.UP : Direction.DOWN;
            } else {
                direction = (c == 0) ? Direction.LEFT : Direction.RIGHT;
            }
            // cannot back onto source
            Coordinate adj = coord.add(direction.getOpposite().getOffset());
            if (adj.equals(sourceCell.coord)) {
                continue;
            }
            break;
        } while (true);
        return new TerminationCell.CreateInfo(coord, direction);
    }

    /** Prints the map to stdout. */
    public void display() {
        int pad = Integer.toString(rows - 1).length();
        Runnable printCols = () -> {
            System.out.print(StringUtils.createPadding(pad, ' '));
            System.out.print(' ');
            for (int i = 0; i < cols - 2; i++) {
                System.out.print((char) ('A' + i));
            }
            System.out.println();
        };

        printCols.run();
        for (int r = 0; r < rows; r++) {
            if (r != 0 && r != rows - 1) {
                System.out.print(String.format("%1$" + pad + "s", r));
            } else {
                System.out.print(StringUtils.createPadding(pad, ' '));
            }
            for (Cell cell : cells[r]) {
                System.out.print(cell.toSingleChar());
            }
            if (r != 0 && r != rows - 1) {
                System.out.print(r);
            }
            System.out.println();
        }
        printCols.run();
    }

    /** Fills the source cell at game start. */
    public void fillBeginTile() {
        sourceCell.setFilled();
        filledTiles.add(sourceCell.coord);
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
    }

    /**
     * The game is lost when no new pipes are filled during a round.
     *
     * @return true if no additional pipes were filled since last round.
     */
    public boolean hasLost() {
        return filledTiles.size() == prevFilledTiles;
    }

    /** Dummy no-op to satisfy some deserializer signatures. */
    public void Map(int rows, int cols) { /* no-op */ }

    /**
     * Constructs the map from a 2D cell array.
     * Validates exactly one SOURCE (non‑edge) and one SINK (edge).
     */
    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = Objects.requireNonNull(cells, "cells must not be null");

        TerminationCell foundSource = null;
        TerminationCell foundSink   = null;
        for (int r = 0; r < rows; r++) {
            if (cells[r].length != cols) {
                throw new IllegalArgumentException(
                        "Row " + r + " length (" + cells[r].length + ") != cols (" + cols + ")");
            }
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell tc) {
                    boolean edge = (r == 0 || r == rows - 1 || c == 0 || c == cols - 1);
                    if (tc.type == TerminationCell.Type.SOURCE) {
                        if (edge) {
                            throw new IllegalArgumentException("SOURCE must be non-edge, but was at " + r + "," + c);
                        }
                        if (foundSource != null) {
                            throw new IllegalArgumentException("Multiple SOURCE cells found");
                        }
                        foundSource = tc;
                    } else {
                        if (!edge) {
                            throw new IllegalArgumentException("SINK must be on edge, but was at " + r + "," + c);
                        }
                        if (foundSink != null) {
                            throw new IllegalArgumentException("Multiple SINK cells found");
                        }
                        foundSink = tc;
                    }
                }
            }
        }
        if (foundSource == null) {
            throw new IllegalArgumentException("No SOURCE cell found");
        }
        if (foundSink   == null) {
            throw new IllegalArgumentException("No SINK cell found");
        }

        // SOURCE must not point into a wall
        {
            Coordinate sc = foundSource.coord;
            Coordinate adj = sc.add(foundSource.pointingTo.getOffset());
            if (adj.row >= 0 && adj.row < rows && adj.col >= 0 && adj.col < cols
                && cells[adj.row][adj.col] instanceof Wall) {
                throw new IllegalArgumentException("SOURCE at " + sc + " points into a wall at " + adj);
            }
        }
        // SINK must point outside
        {
            Coordinate kc = foundSink.coord;
            Coordinate adj = kc.add(foundSink.pointingTo.getOffset());
            if (adj.row >= 0 && adj.row < rows && adj.col >= 0 && adj.col < cols) {
                throw new IllegalArgumentException(
                        "SINK at " + kc + " must point outside map, but points to " + adj);
            }
        }
        this.sourceCell = foundSource;
        this.sinkCell   = foundSink;
    }

    /**
     * Undoes the last pipe placed on the given coordinate.
     * Resets the cell to empty.
     */
    public void undo(final Coordinate coord) {
        int r = coord.row;
        int c = coord.col;
        Cell old = cells[r][c];
        if (!(old instanceof FillableCell)) {
            throw new IllegalArgumentException("Cell at " + coord + " is not fillable: " + old);
        }
        cells[r][c] = new FillableCell(coord);
        if (filledTiles.remove(coord)) {
            prevFilledTiles = filledTiles.size();
        }
        prevFilledDistance = null;
    }

    /**
     * Fills all pipes within {@code distance} steps from the source.
     */
    public void fillTiles(int distance) {
        if (!Objects.equals(prevFilledDistance, distance)) {
            filledTiles.clear();
            prevFilledTiles = 0;
        }
        if (distance == 0) {
            sourceCell.setFilled();
            filledTiles.add(sourceCell.coord);
        } else {
            Queue<Coordinate> q = new ArrayDeque<>(filledTiles);
            Set<Coordinate> seen = new HashSet<>(filledTiles);
            int steps = 0;
            while (steps < distance && !q.isEmpty()) {
                for (int sz = q.size(); sz > 0; sz--) {
                    Coordinate cur = q.remove();
                    for (Direction dir : Direction.values()) {
                        Coordinate nxt = cur.add(dir.getOffset());
                        if (nxt.row < 0 || nxt.row >= rows || nxt.col < 0 || nxt.col >= cols) {
                            continue;
                        }
                        Cell cell = cells[nxt.row][nxt.col];
                        if (!(cell instanceof FillableCell)) {
                            continue;
                        }
                        FillableCell fc = (FillableCell) cell;
                        if (fc.getPipe().isPresent() && !filledTiles.contains(nxt) && seen.add(nxt)) {
                            q.add(nxt);
                        }
                    }
                }
                steps++;
            }
            while (!q.isEmpty()) {
                Coordinate coord = q.remove();
                FillableCell fc = (FillableCell) cells[coord.row][coord.col];
                Pipe pipe = fc.getPipe().orElseThrow();
                pipe.setFilled();
                filledTiles.add(coord);
            }
        }
        prevFilledDistance = distance;
    }

    /**
     * Checks for a continuous path from source to sink by following filled pipes.
     *
     * @return true if a valid path exists.
     */
    public boolean checkPath() {
        Deque<Coordinate> queue = new ArrayDeque<>();
        Set<Coordinate> visited = new HashSet<>();

        sourceCell.setFilled();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.removeFirst();
            if (cur.equals(sinkCell.coord)) {
                sinkCell.setFilled();
                return true;
            }
            Cell cell = cells[cur.row][cur.col];
            Direction[] dirs;
            if (cell instanceof TerminationCell tc) {
                dirs = new Direction[]{tc.pointingTo};
            } else if (cell instanceof FillableCell fc) {
                var optPipe = fc.getPipe();
                if (optPipe.isEmpty() || !optPipe.get().getFilled()) {
                    continue;
                }
                dirs = optPipe.get().getConnections();
            } else {
                continue;
            }

            for (Direction d : dirs) {
                Coordinate next = cur.add(d.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                if (!visited.add(next)) {
                    continue;
                }
                Cell nbr = cells[next.row][next.col];
                if (nbr instanceof TerminationCell tc2) {
                    if (tc2.type == TerminationCell.Type.SINK && tc2.pointingTo.getOpposite() == d) {
                        queue.add(next);
                    }
                } else if (nbr instanceof FillableCell fc2) {
                    var opt2 = fc2.getPipe();
                    if (opt2.isPresent() && opt2.get().getFilled()) {
                        for (Direction back : opt2.get().getConnections()) {
                            if (back == d.getOpposite()) {
                                queue.add(next);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}