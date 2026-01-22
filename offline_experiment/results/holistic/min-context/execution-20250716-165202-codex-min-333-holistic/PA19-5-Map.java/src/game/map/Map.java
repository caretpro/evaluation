
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

    /** Already‐filled coordinates during the game. */
    @NotNull
    private final Set<Coordinate> filledTiles = new HashSet<>();
    private int prevFilledTiles = 0;
    private Integer prevFilledDistance;

    /**
     * Creates a random map with size rows x cols.
     *
     * @param rows Number of rows (≥3).
     * @param cols Number of columns (≥3).
     */
    public Map(int rows, int cols) {
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map must be at least 3×3");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        // Fill everything with walls first
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Wall(new Coordinate(r, c));
            }
        }

        // Place exactly one interior source (pointing inward)
        TerminationCell.CreateInfo startInfo = generateStartCellInfo();
        sourceCell = TerminationCell.sourceOf(startInfo.coord(), startInfo.direction());
        cells[startInfo.coord().row()][startInfo.coord().col()] = sourceCell;

        // Place exactly one edge sink (pointing outward)
        TerminationCell.CreateInfo endInfo = generateEndCellInfo();
        sinkCell = TerminationCell.sinkOf(endInfo.coord(), endInfo.direction());
        cells[endInfo.coord().row()][endInfo.coord().col()] = sinkCell;
    }

    /**
     * Creates a map with the given cells.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param cells Prebuilt cell grid.
     */
    public Map(int rows, int cols, Cell[][] cells) {
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map must be at least 3×3");
        }
        if (cells.length != rows) {
            throw new IllegalArgumentException("cells row count mismatch");
        }
        for (Cell[] row : cells) {
            if (row.length != cols) {
                throw new IllegalArgumentException("cells column count mismatch");
            }
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        TerminationCell foundSource = null;
        TerminationCell foundSink = null;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                this.cells[r][c] = cell;
                if (cell instanceof TerminationCell tc) {
                    if (tc.isSource()) {
                        if (foundSource != null) {
                            throw new IllegalArgumentException("Multiple source cells");
                        }
                        foundSource = tc;
                    } else {
                        if (foundSink != null) {
                            throw new IllegalArgumentException("Multiple sink cells");
                        }
                        foundSink = tc;
                    }
                }
            }
        }
        if (foundSource == null || foundSink == null) {
            throw new IllegalArgumentException("Source or sink missing");
        }

        // Validate source interior, not pointing into a wall
        Coordinate sCoord = foundSource.getCoord();
        Direction sDir = foundSource.getDirection();
        if (sCoord.row() == 0 || sCoord.row() == rows - 1
            || sCoord.col() == 0 || sCoord.col() == cols - 1) {
            throw new IllegalArgumentException("Source must be interior");
        }
        Cell nextFromSource = this.cells[sCoord.row() + sDir.getOffset().row()][sCoord.col() + sDir.getOffset().col()];
        if (nextFromSource instanceof Wall) {
            throw new IllegalArgumentException("Source points into a wall");
        }

        // Validate sink edge, pointing outside
        Coordinate tCoord = foundSink.getCoord();
        Direction tDir = foundSink.getDirection();
        if (!(tCoord.row() == 0 || tCoord.row() == rows - 1
              || tCoord.col() == 0 || tCoord.col() == cols - 1)) {
            throw new IllegalArgumentException("Sink must be on edge");
        }
        int outsideRow = tCoord.row() + tDir.getOffset().row();
        int outsideCol = tCoord.col() + tDir.getOffset().col();
        if (outsideRow >= 0 && outsideRow < rows && outsideCol >= 0 && outsideCol < cols) {
            throw new IllegalArgumentException("Sink does not point outside");
        }

        sourceCell = foundSource;
        sinkCell   = foundSink;
    }

    /**
     * Convenience for unit tests: build from a string.
     */
    @NotNull
    static Map fromString(int rows, int cols, @NotNull String cellsRep) {
        Cell[][] grid = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, grid);
    }

    /**
     * Tries to place a pipe at (row,col).
     */
    public boolean tryPlacePipe(@NotNull Coordinate coord, @NotNull Pipe pipe) {
        return tryPlacePipe(coord.row(), coord.col(), pipe);
    }

    boolean tryPlacePipe(int row, int col, Pipe pipe) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell fc)) {
            return false;
        }
        if (fc.hasPipe()) {
            return false;
        }
        fc.placePipe(pipe);
        return true;
    }

    @NotNull
    private TerminationCell.CreateInfo generateStartCellInfo() {
        Random rng = new Random();
        do {
            int r = rng.nextInt(rows);
            int c = rng.nextInt(cols);
            if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                continue; // must be interior
            }
            Direction dir = Direction.values()[rng.nextInt(Direction.values().length)];
            Coordinate coord = new Coordinate(r, c);
            Coordinate adj = coord.add(dir.getOffset());
            if (adj.row() < 0 || adj.row() >= rows || adj.col() < 0 || adj.col() >= cols) {
                continue; // needs room inward
            }
            return new TerminationCell.CreateInfo(coord, dir);
        } while (true);
    }

    @NotNull
    private TerminationCell.CreateInfo generateEndCellInfo() {
        Random rng = new Random();
        do {
            boolean clampRow = rng.nextBoolean();
            int r = clampRow ? (rng.nextBoolean() ? rows - 1 : 0)
                             : rng.nextInt(rows - 2) + 1;
            int c = !clampRow ? (rng.nextBoolean() ? cols - 1 : 0)
                              : rng.nextInt(cols - 2) + 1;
            Coordinate coord = new Coordinate(r, c);
            if (coord.equals(sourceCell.getCoord())) {
                continue; // don't overlap source
            }
            Direction dir = clampRow
                            ? (r == 0 ? Direction.UP : Direction.DOWN)
                            : (c == 0 ? Direction.LEFT : Direction.RIGHT);
            return new TerminationCell.CreateInfo(coord, dir);
        } while (true);
    }

    /**
     * Displays the current map.
     */
    public void display() {
        final int padLen = Integer.toString(rows - 1).length();
        Runnable printCols = () -> {
            System.out.print(StringUtils.createPadding(padLen, ' '));
            System.out.print(' ');
            for (int i = 0; i < cols - 2; i++) {
                System.out.print((char) ('A' + i));
            }
            System.out.println();
        };
        printCols.run();
        for (int r = 0; r < rows; r++) {
            if (r != 0 && r != rows - 1) {
                System.out.print(String.format("%1$" + padLen + "s", r));
            } else {
                System.out.print(StringUtils.createPadding(padLen, ' '));
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

    /**
     * Undoes a step: resets a FillableCell at coord back to empty.
     */
    public void undo(final Coordinate coord) {
        Cell cell = cells[coord.row()][coord.col()];
        if (!(cell instanceof FillableCell fc)) {
            throw new IllegalArgumentException("Cannot undo non-fillable cell");
        }
        if (!fc.hasPipe()) {
            return;
        }
        cells[coord.row()][coord.col()] = fc.getEmptyCellFactory().apply(coord);
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
        filledTiles.add(sourceCell.getCoord());
    }

    /**
     * Flood‑fill pipes up to the given distance from the source.
     */
    public void fillTiles(int distance) {
        prevFilledTiles = filledTiles.size();
        prevFilledDistance = distance;

        Deque<Coordinate> queue = new ArrayDeque<>();
        Map<Coordinate,Integer> dist = new HashMap<>();

        Coordinate start = sourceCell.getCoord();
        queue.add(start);
        dist.put(start, 0);
        sourceCell.setFilled();
        filledTiles.add(start);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            int d = dist.get(cur);
            if (d >= distance) break;
            Cell c = cells[cur.row()][cur.col()];
            if (!(c instanceof FillableCell fc) || !fc.hasPipe()) continue;
            for (Direction out : fc.getPipe().getConnections()) {
                Coordinate nxt = cur.add(out.getOffset());
                if (nxt.row() < 0 || nxt.row() >= rows || nxt.col() < 0 || nxt.col() >= cols) {
                    continue;
                }
                if (filledTiles.contains(nxt)) continue;
                Cell nc = cells[nxt.row()][nxt.col()];
                if (nc instanceof FillableCell nfc
                    && nfc.hasPipe()
                    && nfc.getPipe().getConnections().contains(out.getOpposite())) {
                    nfc.setFilled();
                    filledTiles.add(nxt);
                    dist.put(nxt, d + 1);
                    queue.add(nxt);
                }
            }
        }
    }

    /**
     * BFS from source along connected pipes to find sink.
     */
    public boolean checkPath() {
        Set<Coordinate> visited = new HashSet<>();
        Deque<Coordinate> queue = new ArrayDeque<>();
        queue.add(sourceCell.getCoord());
        visited.add(sourceCell.getCoord());

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            Cell c = cells[cur.row()][cur.col()];
            if (!(c instanceof FillableCell fc) || !fc.hasPipe()) continue;
            for (Direction out : fc.getPipe().getConnections()) {
                Coordinate nxt = cur.add(out.getOffset());
                if (nxt.equals(sinkCell.getCoord())) {
                    return true;
                }
                if (nxt.row() < 0 || nxt.row() >= rows || nxt.col() < 0 || nxt.col() >= cols) {
                    continue;
                }
                if (visited.contains(nxt)) continue;
                Cell nc = cells[nxt.row()][nxt.col()];
                if (nc instanceof FillableCell nfc
                    && nfc.hasPipe()
                    && nfc.getPipe().getConnections().contains(out.getOpposite())) {
                    visited.add(nxt);
                    queue.add(nxt);
                }
            }
        }
        return false;
    }

    /**
     * The game is lost if in the last fillTiles() no new tile was filled.
     */
    public boolean hasLost() {
        return prevFilledDistance != null && filledTiles.size() == prevFilledTiles;
    }
}