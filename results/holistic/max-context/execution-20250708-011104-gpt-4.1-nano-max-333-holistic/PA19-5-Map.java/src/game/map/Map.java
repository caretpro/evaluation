
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

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new FillableCell(new Coordinate(r, c));
            }
        }

        TerminationCell.CreateInfo startInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(startInfo.coord, startInfo.dir, TerminationCell.Type.SOURCE);
        cells[startInfo.coord.row][startInfo.coord.col] = sourceCell;

        TerminationCell.CreateInfo endInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(endInfo.coord, endInfo.dir, TerminationCell.Type.SINK);
        cells[endInfo.coord.row][endInfo.coord.col] = sinkCell;
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
            throw new IllegalArgumentException("Map must contain one source and one sink cell");
        }
    }

    public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    boolean tryPlacePipe(int row, int col, Pipe p) {
        int r = row;
        int c = col;
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            return false;
        }
        Cell cell = cells[r][c];
        if (!(cell instanceof FillableCell)) {
            return false;
        }
        FillableCell fillable = (FillableCell) cell;
        if (fillable.getMapElement() != null) {
            return false;
        }
        fillable.setMapElement(p);
        return true;
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
            int row = axisToClamp ? (rng.nextInt(2) == 1 ? rows - 1 : 0) : rng.nextInt(rows - 2) + 1;
            int col = !axisToClamp ? (rng.nextInt(2) == 1 ? cols - 1 : 0) : rng.nextInt(cols - 2) + 1;

            if (row == col) continue;

            coord = new Coordinate(row, col);

            if (axisToClamp) {
                direction = (row == 0) ? Direction.UP : Direction.DOWN;
            } else {
                direction = (col == 0) ? Direction.LEFT : Direction.RIGHT;
            }

            Coordinate adjacent = coord.add(direction.getOpposite().getOffset());
            if (adjacent.equals(sourceCell.coord)) continue;

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

        for (int i = 0; i < rows; i++) {
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
        FillableCell fillable = (FillableCell) cell;
        fillable.setMapElement(null);
    }

    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
    }

    public void fillTiles(int distance) {
        filledTiles.clear();
        if (sourceCell == null) return;

        Queue<Coordinate> queue = new LinkedList<>();
        Map<Coordinate, Integer> visited = new HashMap<>();

        queue.add(sourceCell.coord);
        visited.put(sourceCell.coord, 0);
        if (sourceCell instanceof FillableCell) {
            ((FillableCell) sourceCell).setFilled();
        }

        filledTiles.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            int currentDist = visited.get(current);
            if (currentDist >= distance) continue;

            Cell cell = cells[current.row][current.col];
            if (cell instanceof FillableCell) {
                ((FillableCell) cell).setFilled();
            }

            for (Direction dir : Direction.values()) {
                Coordinate neighbor = current.add(dir.getOffset());
                if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                    continue;
                }
                if (visited.containsKey(neighbor)) continue;

                Cell neighborCell = cells[neighbor.row][neighbor.col];
                if (neighborCell instanceof FillableCell) {
                    visited.put(neighbor, currentDist + 1);
                    queue.add(neighbor);
                    filledTiles.add(neighbor);
                }
            }
        }
    }

    public boolean checkPath() {
        if (sourceCell == null || sinkCell == null) return false;

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

            if (cell instanceof TerminationCell) {
                TerminationCell term = (TerminationCell) cell;
                if (term.type == TerminationCell.Type.SINK && current.equals(sinkCell.coord)) {
                    return true;
                }
            }

            if (cell instanceof Pipe) {
                Pipe pipe = (Pipe) cell;
                for (Direction dir : pipe.getConnections()) {
                    Coordinate neighbor = current.add(dir.getOffset());
                    if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                        continue;
                    }
                    if (visited.contains(neighbor)) continue;

                    Cell neighborCell = cells[neighbor.row][neighbor.col];

                    if (neighborCell instanceof Pipe) {
                        Pipe neighborPipe = (Pipe) neighborCell;
                        if (Arrays.asList(neighborPipe.getConnections()).contains(dir.getOpposite())) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    } else if (neighborCell instanceof TerminationCell) {
                        TerminationCell term = (TerminationCell) neighborCell;
                        if (term.type == TerminationCell.Type.SINK && neighbor.equals(sinkCell.coord)) {
                            return true;
                        }
                    }
                }
            } else if (cell instanceof TerminationCell) {
                TerminationCell term = (TerminationCell) cell;
                if (term.type == TerminationCell.Type.SOURCE) {
                    for (Direction dir : Direction.values()) {
                        Coordinate neighbor = current.add(dir.getOffset());
                        if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
                            continue;
                        }
                        if (visited.contains(neighbor)) continue;

                        Cell neighborCell = cells[neighbor.row][neighbor.col];
                        if (neighborCell instanceof Pipe) {
                            Pipe pipe = (Pipe) neighborCell;
                            if (Arrays.asList(pipe.getConnections()).contains(dir)) {
                                visited.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasLost() {
        return prevFilledTiles == filledTiles.size();
    }
}