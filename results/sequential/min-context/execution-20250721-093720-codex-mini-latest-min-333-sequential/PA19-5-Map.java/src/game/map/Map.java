
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
        if (prevFilledDistance == null) {
            return false;
        }
        return filledTiles.size() == prevFilledTiles;
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
            throw new IllegalArgumentException("Map must be at least 3x3");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Wall();
            }
        }
        var srcInfo = generateStartCellInfo();
        sourceCell = TerminationCell.createSource(srcInfo.coord, srcInfo.direction);
        cells[srcInfo.coord.row][srcInfo.coord.col] = sourceCell;
        var sinkInfo = generateEndCellInfo();
        sinkCell = TerminationCell.createSink(sinkInfo.coord, sinkInfo.direction);
        cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c] instanceof Wall) {
                    cells[r][c] = new FillableCell();
                }
            }
        }
    }

    /**
     * Constructs a map from an existing cell grid.
     * Used by Map.fromString(...) to build a map from its textual representation.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param cells Grid of pre-initialized cells.
     */
    public Map(int rows, int cols, @NotNull Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
        // locate source and sink in the provided grid:
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                if (cell instanceof TerminationCell tc) {
                    if (tc.isSource()) {
                        sourceCell = tc;
                    } else {
                        sinkCell = tc;
                    }
                }
            }
        }
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

    /* remaining methods unchanged... */
}