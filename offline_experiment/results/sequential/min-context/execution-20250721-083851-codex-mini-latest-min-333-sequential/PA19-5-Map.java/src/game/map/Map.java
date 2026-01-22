
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

    @NotNull
    private TerminationCell.CreateInfo generateStartCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            int row = rng.nextInt(rows);
            int col = rng.nextInt(cols);
            coord = new Coordinate(row, col);

            int dir = rng.nextInt(4);
            direction = Direction.values()[dir];

            if (row == 0 || row == rows - 1) {
                continue;
            }
            if (col == 0 || col == cols - 1) {
                continue;
            }
            switch (direction) {
                case UP:
                    if (row <= 1) {
                        continue;
                    }
                    break;
                case DOWN:
                    if (row >= rows - 2) {
                        continue;
                    }
                    break;
                case LEFT:
                    if (col <= 1) {
                        continue;
                    }
                    break;
                case RIGHT:
                    if (col >= cols - 2) {
                        continue;
                    }
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
            // false -> X-axis, true -> Y-axis
            boolean axisToClamp = rng.nextInt(2) == 1;
            int row = axisToClamp ? (rng.nextInt(2) == 1 ? rows - 1 : 0) : rng.nextInt(rows - 2) + 1;
            int col = !axisToClamp ? (rng.nextInt(2) == 1 ? cols - 1 : 0) : rng.nextInt(cols - 2) + 1;

            if (row == col) {
                continue;
            }

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

            var adjacentCell = coord.add(direction.getOpposite().getOffset());
            if (adjacentCell.equals(sourceCell.coord)) {
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
        // TODO
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
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
        // TODO
        return false;
    }

    /**
     * Creates a map with size of rows x cols.
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
            throw new IllegalArgumentException("Map must be at least 3×3 to have a valid source and sink");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Wall();
            }
        }
        TerminationCell.CreateInfo startInfo = generateStartCellInfo();
        sourceCell = TerminationCell.createStart(startInfo.coord, startInfo.direction);
        cells[startInfo.coord.row][startInfo.coord.col] = sourceCell;
        TerminationCell.CreateInfo endInfo = generateEndCellInfo();
        sinkCell = TerminationCell.createEnd(endInfo.coord, endInfo.direction);
        cells[endInfo.coord.row][endInfo.coord.col] = sinkCell;
    }

    /**
     * Creates a map with the given cells.
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
            throw new IllegalArgumentException("Map must be at least 3×3");
        }
        if (cells == null || cells.length != rows) {
            throw new IllegalArgumentException("Cells array has wrong number of rows");
        }
        for (Cell[] row : cells) {
            if (row == null || row.length != cols) {
                throw new IllegalArgumentException("Cells array has wrong number of columns");
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
                    boolean edgeRow = (r == 0 || r == rows - 1);
                    boolean edgeCol = (c == 0 || c == cols - 1);
                    Direction dir = tc.getDirection();
                    if (!edgeRow && !edgeCol) {
                        if (foundSource != null) {
                            throw new IllegalArgumentException("Multiple source cells");
                        }
                        if (cells[r + dir.getOffset().row][c + dir.getOffset().col] instanceof Wall) {
                            throw new IllegalArgumentException("Source points into a wall");
                        }
                        foundSource = tc;
                    } else {
                        if (foundSink != null) {
                            throw new IllegalArgumentException("Multiple sink cells");
                        }
                        int nr = r + dir.getOffset().row;
                        int nc = c + dir.getOffset().col;
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
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
     * Tries to place a pipe at (row, col).
     * <p>
     * Note: You cannot overwrite the pipe of a cell once the cell is occupied.
     * </p>
     * <p>
     * Hint: Remember to check whether the map coordinates are within bounds, and whether the target cell
     * is a {@link FillableCell}.
     * </p>
     *
     * @param row One-Based row number to place pipe at.
     * @param col One-Based column number to place pipe at.
     * @param p   Pipe to place in cell.
     * @return {@code true} if the pipe is placed in the cell, {@code false} otherwise.
     */
    boolean tryPlacePipe(int row, int col, Pipe p) {
        int r = row - 1;
        int c = col - 1;
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
        fillable.setPipe(p);
        filledTiles.add(new Coordinate(r, c));
        return true;
    }

    /**
     * Fills all pipes that are within {@code distance} units from the {@code sourceCell}.
     * <p>
     * Hint: There are two ways to approach this. You can either iteratively fill the tiles by distance
     * (i.e. filling distance=0, distance=1, etc), or you can save the tiles you have already filled,
     * and fill all adjacent cells of the already‑filled tiles. Whichever method you choose is up to you,
     * as long as the result is the same.
     * </p>
     *
     * @param distance Distance to fill pipes.
     */
    public void fillTiles(int distance) {
        filledTiles.clear();
        prevFilledTiles = 0;
        prevFilledDistance = distance;
        Deque<Map.Entry<Coordinate, Integer>> q = new ArrayDeque<>();
        Coordinate start = sourceCell.getCoord();
        q.add(Map.entry(start, 0));
        filledTiles.add(start);
        sourceCell.setFilled();
        while (!q.isEmpty()) {
            Map.Entry<Coordinate, Integer> e = q.removeFirst();
            Coordinate coord = e.getKey();
            int d = e.getValue();
            if (d == distance) {
                continue;
            }
            for (Direction dir : Direction.values()) {
                Coordinate nbr = coord.add(dir.getOffset());
                if (nbr.row < 0 || nbr.row >= rows || nbr.col < 0 || nbr.col >= cols) {
                    continue;
                }
                Cell c = cells[nbr.row][nbr.col];
                if (c instanceof FillableCell fillable && fillable.hasPipe() && !filledTiles.contains(nbr)) {
                    fillable.setFilled();
                    filledTiles.add(nbr);
                    q.add(Map.entry(nbr, d + 1));
                }
            }
        }
        prevFilledTiles = filledTiles.size();
    }

    /**
     * Checks whether there exists a path from {@code sourceCell} to {@code sinkCell}.
     * <p>
     * The game is won when the player must place pipes on the map such that a path is formed from
     * the source tile to the sink tile. One of the approaches to check this is to use Breadth First
     * Search to search for the sink tile along the pipes. You may also use other algorithms or
     * create your own, provided it achieves the same goal. The game is lost when no additional pipes
     * are filled in each round after the Nth round. The value of N` can be configured in the loaded
     * map. In the example maps, N` is set to 10.
     * </p>
     *
     * @return {@code true} if a path exists, else {@code false}.
     */
    public boolean checkPath() {
        Deque<Coordinate> queue = new ArrayDeque<>();
        Set<Coordinate> visited = new HashSet<>();
        Coordinate next = sourceCell.getCoord().add(sourceCell.getDirection().getOffset());
        queue.add(next);
        visited.add(sourceCell.getCoord());
        while (!queue.isEmpty()) {
            Coordinate coord = queue.removeFirst();
            if (coord.row < 0 || coord.row >= rows || coord.col < 0 || coord.col >= cols) {
                continue;
            }
            Cell cell = cells[coord.row][coord.col];
            if (cell == sinkCell) {
                return true;
            }
            if (cell instanceof FillableCell fillable && fillable.isFilled()) {
                Pipe pipe = fillable.getPipe();
                for (Direction out : pipe.getOpenEnds()) {
                    Coordinate nbr = coord.add(out.getOffset());
                    if (visited.add(coord) && !visited.contains(nbr)) {
                        if (nbr.row >= 0 && nbr.row < rows && nbr.col >= 0 && nbr.col < cols) {
                            Cell c2 = cells[nbr.row][nbr.col];
                            if (c2 instanceof FillableCell f2 && f2.isFilled()) {
                                if (f2.getPipe().getOpenEnds().contains(out.getOpposite())) {
                                    queue.add(nbr);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}