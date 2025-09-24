
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

        // Initialize all cells as empty FillableCells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new FillableCell(new Coordinate(r, c));
            }
        }

        // Generate start and end cells
        TerminationCell.CreateInfo startInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(startInfo.coord, startInfo.dir, TerminationCell.Type.SOURCE);
        cells[sourceCell.coord.row][sourceCell.coord.col] = sourceCell;

        TerminationCell.CreateInfo endInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(endInfo.coord, endInfo.dir, TerminationCell.Type.SINK);
        cells[sinkCell.coord.row][sinkCell.coord.col] = sinkCell;
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

        // Copy the provided cells
        for (int r = 0; r < rows; r++) {
            System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
        }

        // Find source and sink cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = this.cells[r][c];
                if (cell instanceof TerminationCell) {
                    TerminationCell term = (TerminationCell) cell;
                    if (term.type == TerminationCell.Type.SOURCE) {
                        sourceCell = term;
                    } else if (term.type == TerminationCell.Type.SINK) {
                        sinkCell = term;
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
            return false;
        }
        Cell cell = cells[r][c];
        if (!(cell instanceof FillableCell)) {
            return false;
        }
        FillableCell fillable = (FillableCell) cell;
        if (fillable.getFilled()) {
            return false;
        }
        // Place the pipe wrapped in a FillableCell
        cells[r][c] = new FillableCell(new Coordinate(r, c), p);
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
        // Reset to empty FillableCell
        cells[r][c] = new FillableCell(coord);
    }

    /**
     * Fills all pipes that are within {@code distance} units from the {@code sourceCell}.
     * 
     * <p>
     * Hint: There are two ways to approach this. You can either iteratively fill the tiles by distance (i.e. filling 
     * distance=0, distance=1, etc), or you can save the tiles you have already filled, and fill all adjacent cells of 
     * the already-filled tiles. Whichever method you choose is up to you, as long as the result is the same.
     * </p>
     *
     * @param distance Distance to fill pipes.
     */
    public void fillTiles(int distance) {
        // Use BFS to fill tiles up to the specified distance
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);
        int currentDistance = 0;

        while (!queue.isEmpty() && currentDistance <= distance) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Coordinate coord = queue.poll();
                if (filledTiles.contains(coord)) {
                    continue;
                }
                filledTiles.add(coord);
                Cell cell = cells[coord.row][coord.col];
                if (cell instanceof FillableCell) {
                    ((FillableCell) cell).setFilled();
                }
                if (currentDistance < distance) {
                    for (Direction dir : Direction.values()) {
                        Coordinate neighbor = coord.add(dir.getOffset());
                        if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                            if (!visited.contains(neighbor)) {
                                visited.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
            currentDistance++;
        }
    }

    /**
     * Checks whether there exists a path from {@code sourceCell} to {@code sinkCell}.
     * 
     * <p>
     * The game is won when the player must place pipes on the map such that a path is formed from the source tile to the sink tile. One of the approaches to check this is to use Breadth First Search to search for the sink tile along the pipes. You may also use other algorithms or create your own, provided it achieves the same goal.
     * The game is lost when no additional pipes are filled in each round after the Nth round. In the example maps, N' is set to 10.
     * </p>
     *
     * @return {@code true} if a path exists, else {@code false}.
     */
    public boolean checkPath() {
        // BFS from source to sink
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        queue.add(sourceCell.coord);
        visited.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (current.equals(sinkCell.coord)) {
                return true;
            }
            Cell cell = cells[current.row][current.col];
            if (cell instanceof TerminationCell) {
                // Termination cells are endpoints; only proceed if they are the sink
                if (((TerminationCell) cell).type == TerminationCell.Type.SINK && current.equals(sinkCell.coord)) {
                    return true;
                }
                continue;
            }
            if (cell instanceof Pipe || cell instanceof TerminationCell) {
                Direction[] connections;
                if (cell instanceof Pipe) {
                    connections = ((Pipe) cell).getConnections();
                } else {
                    connections = ((TerminationCell) cell).getConnections();
                }
                for (Direction dir : connections) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                        if (!visited.contains(neighbor)) {
                            Cell neighborCell = cells[neighbor.row][neighbor.col];
                            // Check if the neighbor is connectable
                            if (neighborCell instanceof Pipe || neighborCell instanceof TerminationCell) {
                                // For pipes, check if connection is mutual
                                if (neighborCell instanceof Pipe) {
                                    Pipe pipeNeighbor = (Pipe) neighborCell;
                                    Direction[] neighborConnections = pipeNeighbor.getConnections();
                                    if (Arrays.asList(neighborConnections).contains(dir.getOpposite())) {
                                        visited.add(neighbor);
                                        queue.add(neighbor);
                                    }
                                } else if (neighborCell instanceof TerminationCell) {
                                    TerminationCell termNeighbor = (TerminationCell) neighborCell;
                                    // Only proceed if the termination cell points towards current
                                    if (termNeighbor.pointingTo == dir.getOpposite()) {
                                        visited.add(neighbor);
                                        queue.add(neighbor);
                                    }
                                }
                            }
                        }
                    }
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
        // Check if no new tiles were filled in the last fillTiles call
        int currentFilledCount = filledTiles.size();
        boolean lost = (currentFilledCount == prevFilledTiles);
        prevFilledTiles = currentFilledCount;
        return lost;
    }

    // Private methods to generate start and end info
    private TerminationCell.CreateInfo generateStartCellInfo() {
        return generateStartCellInfo();
    }

    private TerminationCell.CreateInfo generateEndCellInfo() {
        return generateEndCellInfo();
    }
}