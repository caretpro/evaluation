
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

        // Initialize all cells as FillableCell (empty)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new FillableCell(new Coordinate(r, c));
            }
        }

        // Generate start and end cells
        CreateInfo startInfo = generateStartCellInfo();
        CreateInfo endInfo = generateEndCellInfo();

        // Create source and sink cells with correct type
        sourceCell = new TerminationCell(startInfo.coord, startInfo.direction, TerminationCell.Type.SOURCE);
        sinkCell = new TerminationCell(endInfo.coord, endInfo.direction, TerminationCell.Type.SINK);

        // Place source and sink in the map
        cells[startInfo.coord.row][startInfo.coord.col] = sourceCell;
        cells[endInfo.coord.row][endInfo.coord.col] = sinkCell;

        // Store source and sink for reference
        this.sourceCell = sourceCell;
        this.sinkCell = sinkCell;
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
        if (cells.length != rows || Arrays.stream(cells).anyMatch(row -> row.length != cols)) {
            throw new IllegalArgumentException("Cells array dimensions do not match specified rows and cols");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        int sourceCount = 0;
        int sinkCount = 0;
        TerminationCell tempSource = null;
        TerminationCell tempSink = null;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                this.cells[r][c] = cell;

                if (cell instanceof TerminationCell) {
                    TerminationCell term = (TerminationCell) cell;
                    if (term.getType() == TerminationCell.Type.SOURCE) {
                        sourceCount++;
                        tempSource = term;
                    } else if (term.getType() == TerminationCell.Type.SINK) {
                        sinkCount++;
                        tempSink = term;
                    }
                }
            }
        }

        if (sourceCount != 1) {
            throw new IllegalArgumentException("Map must contain exactly one source cell");
        }
        if (sinkCount != 1) {
            throw new IllegalArgumentException("Map must contain exactly one sink cell");
        }

        // Verify source is not pointing into a wall
        Coordinate sourceCoord = tempSource.getCoord();
        Direction sourceDir = tempSource.getDirection();
        Coordinate next = sourceCoord.add(sourceDir.getOffset());
        if (isWithinBounds(next) && cells[next.row][next.col] instanceof Wall) {
            throw new IllegalArgumentException("Source cannot point into a wall");
        }

        // Verify sink points outside the map
        Coordinate sinkCoord = tempSink.getCoord();
        Coordinate outside = sinkCoord.add(tempSink.getDirection().getOffset());
        if (isWithinBounds(outside)) {
            throw new IllegalArgumentException("Sink must point outside the map");
        }

        this.sourceCell = tempSource;
        this.sinkCell = tempSink;
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
        if (!isWithinBounds(new Coordinate(r, c))) {
            return false;
        }
        Cell cell = cells[r][c];
        if (!(cell instanceof FillableCell)) {
            return false;
        }
        if (cell instanceof Pipe) {
            return false; // Already occupied
        }
        // Replace with the pipe
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
        if (!isWithinBounds(coord)) {
            throw new IllegalArgumentException("Coordinate out of bounds");
        }
        Cell cell = cells[coord.row][coord.col];
        if (!(cell instanceof FillableCell)) {
            throw new IllegalArgumentException("Cell is not fillable");
        }
        // Reset to empty fillable cell
        cells[coord.row][coord.col] = new FillableCell(coord);
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
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        queue.add(sourceCell.getCoord());
        visited.add(sourceCell.getCoord());
        int currentDistance = 0;

        while (!queue.isEmpty() && currentDistance <= distance) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                Coordinate coord = queue.poll();
                filledTiles.add(coord);
                Cell cell = cells[coord.row][coord.col];
                if (cell instanceof FillableCell) {
                    ((FillableCell) cell).setFilled();
                }
                if (currentDistance < distance) {
                    for (Direction dir : Direction.values()) {
                        Coordinate neighbor = coord.add(dir.getOffset());
                        if (isWithinBounds(neighbor) && !visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
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
     * The game is lost when no additional pipes are filled in each round after the Nth round. In the example maps, N` is set to 10.
     * </p>
     *
     * @return {@code true} if a path exists, else {@code false}.
     */
    public boolean checkPath() {
        Queue<Coordinate> queue = new LinkedList<>();
        Set<Coordinate> visited = new HashSet<>();
        queue.add(sourceCell.getCoord());
        visited.add(sourceCell.getCoord());

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (current.equals(sinkCell.getCoord())) {
                return true;
            }
            Cell cell = cells[current.row][current.col];
            List<Coordinate> neighbors = getConnectedNeighbors(current, cell);
            for (Coordinate neighbor : neighbors) {
                if (isWithinBounds(neighbor) && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }

    /**
     * Helper to get connected neighbors based on current cell's pipes and directions.
     */
    private List<Coordinate> getConnectedNeighbors(Coordinate coord, Cell cell) {
        List<Coordinate> neighbors = new ArrayList<>();
        if (cell instanceof Pipe) {
            Pipe pipe = (Pipe) cell;
            for (Direction dir : pipe.getConnectedDirections()) {
                Coordinate neighbor = coord.add(dir.getOffset());
                if (isWithinBounds(neighbor)) {
                    Cell neighborCell = cells[neighbor.row][neighbor.col];
                    if (neighborCell instanceof Pipe || neighborCell instanceof TerminationCell) {
                        if (neighborCell instanceof Pipe) {
                            Pipe neighborPipe = (Pipe) neighborCell;
                            if (neighborPipe.getConnectedDirections().contains(dir.getOpposite())) {
                                neighbors.add(neighbor);
                            }
                        } else if (neighborCell instanceof TerminationCell) {
                            TerminationCell term = (TerminationCell) neighborCell;
                            if (term.getCoord().equals(neighbor) && term.getType() == TerminationCell.Type.SINK) {
                                neighbors.add(neighbor);
                            }
                        }
                    }
                }
            }
        } else if (cell instanceof TerminationCell) {
            TerminationCell term = (TerminationCell) cell;
            Direction dir = term.getDirection();
            Coordinate neighbor = coord.add(dir.getOffset());
            if (isWithinBounds(neighbor)) {
                Cell neighborCell = cells[neighbor.row][neighbor.col];
                if (neighborCell instanceof Pipe || neighborCell instanceof TerminationCell) {
                    if (neighborCell instanceof Pipe) {
                        Pipe pipe = (Pipe) neighborCell;
                        if (pipe.getConnectedDirections().contains(dir.getOpposite())) {
                            neighbors.add(neighbor);
                        }
                    } else if (neighborCell instanceof TerminationCell) {
                        TerminationCell neighborTerm = (TerminationCell) neighborCell;
                        if (neighborTerm.getCoord().equals(neighbor) && neighborTerm.getType() == TerminationCell.Type.SINK) {
                            neighbors.add(neighbor);
                        }
                    }
                }
            }
        }
        return neighbors;
    }

    /**
     * Checks whether the game is lost (no pipes filled in the last round).
     */
    public boolean hasLost() {
        return prevFilledTiles == filledTiles.size();
    }

    /**
     * Helper to check if coordinate is within bounds.
     */
    private boolean isWithinBounds(Coordinate coord) {
        return coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols;
    }

    // Make generateStartCellInfo() and generateEndCellInfo() public
    public static class CreateInfo {
        public final Coordinate coord;
        public final Direction direction;

        public CreateInfo(Coordinate coord, Direction direction) {
            this.coord = coord;
            this.direction = direction;
        }
    }

    public CreateInfo generateStartCellInfo() {
        Random rng = new Random();

        Coordinate coord;
        Direction direction;

        do {
            int row = rng.nextInt(rows);
            int col = rng.nextInt(cols);
            coord = new Coordinate(row, col);

            int dirIdx = rng.nextInt(4);
            direction = Direction.values()[dirIdx];

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

        return new CreateInfo(coord, direction);
    }

    public CreateInfo generateEndCellInfo() {
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
            if (adjacent.equals(sourceCell.getCoord())) {
                continue;
            }
            break;
        } while (true);

        return new CreateInfo(coord, direction);
    }
}