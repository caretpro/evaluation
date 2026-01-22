
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
     */
    public Map(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    cells[r][c] = new Wall(new Coordinate(r, c));
                } else {
                    cells[r][c] = new FillableCell(new Coordinate(r, c));
                }
            }
        }

        TerminationCell.CreateInfo startInfo = generateStartCellInfo();
        TerminationCell.CreateInfo endInfo = generateEndCellInfo();

        sourceCell = new TerminationCell(startInfo.coord, startInfo.dir, TerminationCell.Type.SOURCE);
        sinkCell = new TerminationCell(endInfo.coord, endInfo.dir, TerminationCell.Type.SINK);

        cells[sourceCell.coord.row][sourceCell.coord.col] = sourceCell;
        cells[sinkCell.coord.row][sinkCell.coord.col] = sinkCell;
    }

    /**
     * Creates a map with the given cells.
     */
    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        for (int r = 0; r < rows; r++) {
            System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
        }

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

        if (sourceCell == null || sinkCell == null) {
            throw new IllegalArgumentException("Map must contain one source and one sink cell.");
        }
    }

    public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

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
        if (fillable.getOccupied()) {
            return false;
        }
        // Replace the FillableCell with a new Pipe cell
        cells[r][c] = p;
        return true;
    }

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
        cells[r][c] = new FillableCell(coord);
        filledTiles.remove(coord);
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

            int dirIdx = rng.nextInt(4);
            direction = Direction.values()[dirIdx];

            if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
                continue;
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

    @NotNull
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
        cells[r][c] = new FillableCell(coord);
        filledTiles.remove(coord);
    }

    public void fillTiles(int distance) {
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
                Cell cell = cells[coord.row][coord.col];
                if (cell instanceof FillableCell) {
                    ((FillableCell) cell).setFilled();
                    filledTiles.add(coord);
                }
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
            currentDistance++;
        }
    }

    public boolean checkPath() {
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
                continue;
            }
            if (cell instanceof Pipe) {
                Pipe pipe = (Pipe) cell;
                for (Direction dir : pipe.getConnections()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row >= 0 && neighbor.row < rows && neighbor.col >= 0 && neighbor.col < cols) {
                        if (!visited.contains(neighbor)) {
                            Cell neighborCell = cells[neighbor.row][neighbor.col];
                            if (neighborCell instanceof Pipe || neighbor.equals(sinkCell.coord)) {
                                if (neighborCell instanceof Pipe) {
                                    Pipe neighborPipe = (Pipe) neighborCell;
                                    Direction opposite = dir.getOpposite();
                                    if (Arrays.asList(neighborPipe.getConnections()).contains(opposite)) {
                                        visited.add(neighbor);
                                        queue.add(neighbor);
                                    }
                                } else if (neighbor.equals(sinkCell.coord)) {
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
        int currentFilledCount = filledTiles.size();
        boolean lost = currentFilledCount == prevFilledTiles;
        prevFilledTiles = currentFilledCount;
        return lost;
    }
}