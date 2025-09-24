
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
            throw new IllegalArgumentException("Map must be at least 3x3");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        // fill everything with walls
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Wall(new Coordinate(r, c));
            }
        }

        // place the unique source
        TerminationCell.CreateInfo startInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(startInfo.coord, startInfo.dir, TerminationCell.Type.SOURCE);
        cells[startInfo.coord.row][startInfo.coord.col] = sourceCell;

        // place the unique sink
        TerminationCell.CreateInfo endInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(endInfo.coord, endInfo.dir, TerminationCell.Type.SINK);
        cells[endInfo.coord.row][endInfo.coord.col] = sinkCell;

        // convert all interior (non‑edge) cells (except the source) to FillableCell
        for (int r = 1; r < rows - 1; r++) {
            for (int c = 1; c < cols - 1; c++) {
                Coordinate coord = new Coordinate(r, c);
                if (!coord.equals(startInfo.coord)) {
                    cells[r][c] = new FillableCell(coord);
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
        if (rows != cells.length || cols != cells[0].length) {
            throw new IllegalArgumentException("Dimensions mismatch");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        TerminationCell foundSource = null;
        TerminationCell foundSink = null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = Objects.requireNonNull(cells[r][c], "Cell matrix contains null");
                this.cells[r][c] = cell;
                if (cell instanceof TerminationCell tc) {
                    if (tc.type == TerminationCell.Type.SOURCE) {
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
     * @param row One‑based row number to place pipe at.
     * @param col One‑based column number to place pipe at.
     * @param p   Pipe to place in cell.
     * @return {@code true} if the pipe is placed in the cell, {@code false} otherwise.
     */
    boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 1 || row > rows - 2 || col < 1 || col > cols - 2) {
            return false;
        }
        Cell target = cells[row][col];
        if (target instanceof FillableCell fc && !fc.hasPipe()) {
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
            direction = Direction.values()[rng.nextInt(4)];

            if (row == 0 || row == rows - 1) continue;
            if (col == 0 || col == cols - 1) continue;

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
            int row = axisToClamp
                ? (rng.nextInt(2) == 1 ? rows - 1 : 0)
                : rng.nextInt(rows - 2) + 1;
            int col = !axisToClamp
                ? (rng.nextInt(2) == 1 ? cols - 1 : 0)
                : rng.nextInt(cols - 2) + 1;

            coord = new Coordinate(row, col);

            if (axisToClamp) {
                direction = (row == 0 ? Direction.UP : Direction.DOWN);
            } else {
                direction = (col == 0 ? Direction.LEFT : Direction.RIGHT);
            }

            var adjacent = coord.add(direction.getOpposite().getOffset());
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

            Arrays.stream(cells[i]).forEachOrdered(elem -> System.out.print(elem.toSingleChar()));

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
            throw new IllegalArgumentException("Cannot undo non‑fillable cell");
        }
        FillableCell fc = (FillableCell) cell;
        fc.removePipe();
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

    /**
     * Fills all pipes that are within {@code distance} units from the {@code sourceCell}.
     *
     * <p>
     * Hint: There are two ways to approach this. You can either iteratively fill the tiles by distance (i.e. filling
     * distance=0, distance=1, etc), or you can save the tiles you have already filled, and fill all adjacent cells of
     * the already‑filled tiles. Whichever method you choose is up to you, as long as the result is the same.
     * </p>
     *
     * @param distance Distance to fill pipes.
     */
    public void fillTiles(int distance) {
        if (prevFilledDistance != null && distance <= prevFilledDistance) {
            return; // already filled up to this distance
        }
        prevFilledDistance = distance;

        Queue<Coordinate> queue = new ArrayDeque<>();
        Set<Coordinate> visited = new HashSet<>();

        // start from the source
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);

        int depth = 0;
        while (!queue.isEmpty() && depth <= distance) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Coordinate cur = queue.poll();
                Cell cell = cells[cur.row][cur.col];
                if (cell instanceof FillableCell fc && fc.hasPipe()) {
                    fc.getPipe().setFilled();
                    filledTiles.add(cur);
                }
                if (cell instanceof TerminationCell tc && tc.type == TerminationCell.Type.SINK) {
                    continue;
                }
                Direction[] exits;
                if (cell instanceof FillableCell fcell && fcell.hasPipe()) {
                    exits = fcell.getPipe().getConnections();
                } else if (cell instanceof TerminationCell tcell && tcell.type == TerminationCell.Type.SOURCE) {
                    exits = new Direction[]{tcell.pointingTo};
                } else {
                    continue;
                }
                for (Direction dir : exits) {
                    Coordinate next = cur.add(dir.getOpposite().getOffset());
                    if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                        continue;
                    }
                    if (visited.add(next)) {
                        queue.add(next);
                    }
                }
            }
            depth++;
        }

        prevFilledTiles = filledTiles.size();
    }

    /**
     * Checks whether there exists a path from {@code sourceCell} to {@code sinkCell}.
     *
     * <p>
     * The game is won when the player must place pipes on the map such that a path is formed from the source tile to the sink tile. One of the approaches to check this is to use Breadth First Search to search for the sink tile along the pipes. You may also use other algorithms or create your own, provided it achieves the same goal.
     * The game is lost when no additional pipes are filled in each round after the Nth round. The value of N' can be configured in the loaded map. In the example maps, N` is set to 10.
     * </p>
     *
     * @return {@code true} if a path exists, else {@code false}.
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
            Direction[] exits;
            if (cell instanceof FillableCell fc && fc.hasPipe()) {
                exits = fc.getPipe().getConnections();
            } else if (cell instanceof TerminationCell tc && tc.type == TerminationCell.Type.SOURCE) {
                exits = new Direction[]{tc.pointingTo};
            } else {
                continue;
            }
            for (Direction dir : exits) {
                Coordinate next = cur.add(dir.getOffset());
                if (next.row < 0 || next.row >= rows || next.col < 0 || next.col >= cols) {
                    continue;
                }
                Cell neighbor = cells[next.row][next.col];
                Direction back = dir.getOpposite();
                if (neighbor instanceof FillableCell nfc && nfc.hasPipe()) {
                    if (Arrays.asList(nfc.getPipe().getConnections()).contains(back) && visited.add(next)) {
                        queue.add(next);
                    }
                } else if (neighbor instanceof TerminationCell ntc && ntc.type == TerminationCell.Type.SINK
                        && ntc.pointingTo == back && visited.add(next)) {
                    queue.add(next);
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * Hint: The game is lost when a round ends and no pipes are filled during the round. Is
     * there a way to check whether pipes are filled during a round?
     * </p>
     *
     * @return {@code true} if the game is lost.
     */
    public boolean hasLost() {
        // lost if in the last fill round, no new tiles were filled
        return prevFilledDistance != null && !filledTiles.isEmpty() && filledTiles.size() == prevFilledTiles;
    }
}