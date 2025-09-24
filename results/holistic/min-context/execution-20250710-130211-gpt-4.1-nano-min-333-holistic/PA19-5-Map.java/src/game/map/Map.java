
package game.map;

import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.map.cells.TerminationCell;
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

        // Initialize all cells as empty FillableCells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.cells[r][c] = new FillableCell(new Coordinate(r, c));
            }
        }

        // Generate start (source) cell info
        var startInfo = generateStartCellInfo();
        this.sourceCell = new TerminationCell(startInfo.coord, startInfo.direction);
        this.cells[startInfo.coord.row][startInfo.coord.col] = this.sourceCell;

        // Generate end (sink) cell info
        var endInfo = generateEndCellInfo();
        this.sinkCell = new TerminationCell(endInfo.coord, endInfo.direction);
        this.cells[endInfo.coord.row][endInfo.coord.col] = this.sinkCell;
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
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        // Copy provided cells
        for (int r = 0; r < rows; r++) {
            System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
        }

        // Find source and sink cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = this.cells[r][c];
                if (cell instanceof TerminationCell termCell) {
                    if (termCell.isSource()) {
                        this.sourceCell = termCell;
                    } else if (termCell.isSink()) {
                        this.sinkCell = termCell;
                    }
                }
            }
        }
        this.rows = rows;
        this.cols = cols;
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
        int r = row - 1;
        int c = col - 1;
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false; // Out of bounds
        }
        Cell cell = cells[r][c];
        if (!(cell instanceof FillableCell)) {
            return false; // Cannot place pipe here
        }
        if (cell instanceof Pipe) {
            return false; // Already occupied
        }
        cells[r][c] = p;
        return true;
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
        filledTiles.add(sourceCell.coord);
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
    }

    /**
     * Helper method to generate start cell info.
     */
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
                continue; // Must be non-edge
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

    /**
     * Helper method to generate end cell info.
     */
    private TerminationCell.CreateInfo generateEndCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            boolean axisToClamp = rng.nextInt(2) == 1;
            int row, col;
            if (axisToClamp) {
                row = rng.nextInt(2) == 1 ? rows - 1 : 0;
                col = rng.nextInt(rows - 2) + 1;
            } else {
                col = rng.nextInt(2) == 1 ? cols - 1 : 0;
                row = rng.nextInt(cols - 2) + 1;
            }

            coord = new Coordinate(row, col);

            if (row == 0) {
                direction = Direction.UP;
            } else if (row == rows - 1) {
                direction = Direction.DOWN;
            } else if (col == 0) {
                direction = Direction.LEFT;
            } else {
                direction = Direction.RIGHT;
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

    public void fillTiles(int distance) {
        Set<Coordinate> newFilled = new HashSet<>();
        Queue<Coordinate> queue = new LinkedList<>();
        for (Coordinate coord : filledTiles) {
            queue.add(coord);
        }
        Set<Coordinate> visited = new HashSet<>(filledTiles);
        int currentDistance = 0;

        while (!queue.isEmpty() && currentDistance <= distance) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Coordinate current = queue.poll();
                Cell cell = cells[current.row][current.col];
                if (cell instanceof FillableCell) {
                    ((FillableCell) cell).setFilled();
                    newFilled.add(current);
                }
                for (Direction dir : Direction.values()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
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
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (current.equals(sinkCell.coord)) {
                return true;
            }
            Cell cell = cells[current.row][current.col];
            if (cell instanceof Pipe pipe) {
                for (Direction dir : pipe.getConnectedDirections()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                        if (!visited.contains(neighbor)) {
                            Cell neighborCell = cells[neighbor.row][neighbor.col];
                            if (neighborCell instanceof Pipe neighborPipe) {
                                if (neighborPipe.isConnected(dir.getOpposite())) {
                                    visited.add(neighbor);
                                    queue.add(neighbor);
                                }
                            } else if (neighbor.equals(sinkCell.coord)) {
                                return true;
                            }
                        }
                    }
                }
            } else if (cell instanceof TerminationCell termCell) {
                if (current.equals(termCell.coord)) {
                    for (Direction dir : termCell.getConnectedDirections()) {
                        Coordinate neighbor = current.add(dir.getOffset());
                        if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                            Cell neighborCell = cells[neighbor.row][neighbor.col];
                            if (neighborCell instanceof Pipe neighborPipe && neighborPipe.isConnected(dir.getOpposite())) {
                                if (!visited.contains(neighbor)) {
                                    visited.add(neighbor);
                                    queue.add(neighbor);
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles != 0 && prevFilledTiles == filledTiles.size();
    }
}