
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
        this(rows, cols, new Cell[rows][cols]);
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
     * @param rows       Number of rows.
     * @param cols       Number of columns.
     * @param inputCells Cells to fill the map (may include walls).
     */
    public Map(int rows, int cols, Cell[][] inputCells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        // 1) Place the source tile
        TerminationCell.CreateInfo startInfo = generateStartCellInfo();
        this.sourceCell =
            TerminationCell.createSource(startInfo.coord, startInfo.direction);
        Coordinate sc = startInfo.coord;
        cells[sc.row][sc.col] = sourceCell;

        // 2) Place the sink tile
        TerminationCell.CreateInfo endInfo = generateEndCellInfo();
        this.sinkCell =
            TerminationCell.createSink(endInfo.coord, endInfo.direction);
        Coordinate ec = endInfo.coord;
        cells[ec.row][ec.col] = sinkCell;

        // 3) Fill in everything else: walls where provided, otherwise fresh fillable
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] == null) {
                    if (inputCells[r][c] instanceof Wall) {
                        cells[r][c] = inputCells[r][c];
                    } else {
                        cells[r][c] = new FillableCell(r, c);
                    }
                }
            }
        }
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
    public static Map fromString(int rows, int cols, @NotNull String cellsRep) {
        var parsed = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, parsed);
    }

    /**
     * Tries to place a pipe at (row, col).
     *
     * @param coord Coordinate to place pipe at.
     * @param pipe  Pipe to place in cell.
     * @return {@code true} if the pipe is placed in the cell, {@code false} otherwise.
     */
    public boolean tryPlacePipe(@NotNull Coordinate coord, @NotNull Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Tries to place a pipe at (row, col).
     *
     * <p>
     * Note: You cannot overwrite the pipe of a cell once the cell is occupied.
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
        if (!(cell instanceof FillableCell fillable)) {
            return false;
        }
        // cannot overwrite existing pipe
        if (fillable.hasPipe()) {
            return false;
        }
        fillable.setPipe(p);
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
            direction = Direction.values()[rng.nextInt(Direction.values().length)];
            // Must not lie on the edge
            if (r == 0 || r == rows - 1) continue;
            if (c == 0 || c == cols - 1) continue;
            // Must not point into the wall border
            switch (direction) {
                case UP    -> { if (r <= 1)       continue; }
                case DOWN  -> { if (r >= rows - 2) continue; }
                case LEFT  -> { if (c <= 1)       continue; }
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
            boolean clampRow = rng.nextBoolean();
            int r = clampRow
                    ? (rng.nextBoolean() ? rows - 1 : 0)
                    : rng.nextInt(rows - 2) + 1;
            int c = clampRow
                    ? rng.nextInt(cols - 2) + 1
                    : (rng.nextBoolean() ? cols - 1 : 0);
            coord = new Coordinate(r, c);
            if (r == 0)               direction = Direction.UP;
            else if (r == rows - 1)   direction = Direction.DOWN;
            else if (c == 0)          direction = Direction.LEFT;
            else                      direction = Direction.RIGHT;
            // avoid placing sink directly next to source exit
            if (coord.add(direction.getOpposite().getOffset()).equals(sourceCell.coord)) {
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
        final int padLength = Integer.valueOf(rows - 1).toString().length();
        Runnable printCols = () -> {
            System.out.print(StringUtils.createPadding(padLength, ' '));
            System.out.print(' ');
            for (int i = 0; i < cols - 2; i++) {
                System.out.print((char) ('A' + i));
            }
            System.out.println();
        };
        printCols.run();
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
        printCols.run();
    }

    /**
     * Undoes a step from the map.
     *
     * @param coord Coordinate to reset.
     * @throws IllegalArgumentException if the cell is not an instance of {@link FillableCell}.
     */
    public void undo(final Coordinate coord) {
        Cell cell = cells[coord.row][coord.col];
        if (!(cell instanceof FillableCell)) {
            throw new IllegalArgumentException("Cannot undo at non-fillable cell: " + coord);
        }
        cells[coord.row][coord.col] = new FillableCell(coord.row, coord.col);
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
     */
    public void fillTiles(int distance) {
        prevFilledDistance = distance;
        prevFilledTiles   = filledTiles.size();
        Queue<Coordinate> queue = new ArrayDeque<>();
        sourceCell.setFilled();
        queue.add(sourceCell.coord);
        Set<Coordinate> seen = new HashSet<>();
        seen.add(sourceCell.coord);

        int depth = 0;
        while (!queue.isEmpty() && depth <= distance) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Coordinate cur = queue.poll();
                Cell cell = cells[cur.row][cur.col];
                if (!(cell instanceof FillableCell fc) || !fc.hasPipe()) {
                    continue;
                }
                fc.setFilled();
                filledTiles.add(cur);
                for (Direction d : Direction.values()) {
                    Coordinate nxt = cur.add(d.getOffset());
                    if (nxt.row < 0 || nxt.row >= rows || nxt.col < 0 || nxt.col >= cols) {
                        continue;
                    }
                    if (seen.add(nxt)) {
                        queue.add(nxt);
                    }
                }
            }
            depth++;
        }
    }

    /**
     * Checks whether there exists a path from {@code sourceCell} to {@code sinkCell}.
     */
    public boolean checkPath() {
        Queue<Coordinate> queue = new ArrayDeque<>();
        Set<Coordinate> visited = new HashSet<>();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            if (cur.equals(sinkCell.coord)) {
                return true;
            }
            Cell cell = cells[cur.row][cur.col];
            if (!(cell instanceof FillableCell fc) || !fc.isFilled() || !fc.hasPipe()) {
                continue;
            }
            Pipe pipe = fc.getPipe();
            for (Direction d : Direction.values()) {
                if (!pipe.hasOpening(d)) {
                    continue;
                }
                Coordinate nxt = cur.add(d.getOffset());
                if (nxt.row < 0 || nxt.row >= rows || nxt.col < 0 || nxt.col >= cols) {
                    continue;
                }
                Cell neigh = cells[nxt.row][nxt.col];
                if (neigh instanceof FillableCell nfc
                    && nfc.isFilled()
                    && nfc.hasPipe()
                    && nfc.getPipe().hasOpening(d.getOpposite())
                    && visited.add(nxt)) {
                    queue.add(nxt);
                }
                if (neigh instanceof TerminationCell term
                    && term.coord.equals(sinkCell.coord)
                    && term.direction == d) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return {@code true} if in the last fillTiles() round no new tiles were filled.
     */
    public boolean hasLost() {
        return prevFilledDistance != null
            && (filledTiles.size() - prevFilledTiles) == 0;
    }
}