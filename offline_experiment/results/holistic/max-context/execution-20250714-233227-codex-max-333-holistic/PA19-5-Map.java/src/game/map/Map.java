
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
import util.PipePatterns;

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

        // Fill all cells with Wall
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Wall(new Coordinate(r, c));
            }
        }

        // Place the unique source (non-edge) and sink (edge)
        var startInfo = generateStartCellInfo();
        sourceCell = new TerminationCell(startInfo.coord, startInfo.dir, TerminationCell.Type.SOURCE);
        cells[startInfo.coord.row][startInfo.coord.col] = sourceCell;

        var endInfo = generateEndCellInfo();
        sinkCell = new TerminationCell(endInfo.coord, endInfo.dir, TerminationCell.Type.SINK);
        cells[endInfo.coord.row][endInfo.coord.col] = sinkCell;
    }

    /**
     * Creates a map with the given cells.
     */
    public Map(int rows, int cols, Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        TerminationCell foundSource = null;
        TerminationCell foundSink = null;

        for (int r = 0; r < rows; r++) {
            if (cells[r].length != cols) {
                throw new IllegalArgumentException("All rows must have exactly " + cols + " columns");
            }
            for (int c = 0; c < cols; c++) {
                Cell cell = cells[r][c];
                this.cells[r][c] = cell;
                if (cell instanceof TerminationCell tc) {
                    if (tc.type == TerminationCell.Type.SOURCE) {
                        if (foundSource != null) {
                            throw new IllegalArgumentException("Multiple sources are not allowed");
                        }
                        foundSource = tc;
                    } else {
                        if (foundSink != null) {
                            throw new IllegalArgumentException("Multiple sinks are not allowed");
                        }
                        foundSink = tc;
                    }
                }
            }
        }
        if (foundSource == null || foundSink == null) {
            throw new IllegalArgumentException("Both source and sink must be present");
        }
        sourceCell = foundSource;
        sinkCell = foundSink;
    }

    /**
     * Constructs a map from a map string.
     */
    @NotNull
    static Map fromString(int rows, int cols, @NotNull String cellsRep) {
        Cell[][] parsed = Deserializer.parseString(rows, cols, cellsRep);
        return new Map(rows, cols, parsed);
    }

    /**
     * Tries to place a pipe at (row, col).
     */
    public boolean tryPlacePipe(@NotNull Coordinate coord, @NotNull Pipe pipe) {
        return tryPlacePipe(coord.row, coord.col, pipe);
    }

    /**
     * Tries to place a pipe at (row, col).
     */
    boolean tryPlacePipe(int row, int col, Pipe p) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }
        Cell cell = cells[row][col];
        if (!(cell instanceof FillableCell fillable)) {
            return false;
        }
        // Cannot overwrite an existing pipe
        if (fillable.getPipe().isPresent()) {
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
            int r = rng.nextInt(rows), c = rng.nextInt(cols);
            coord = new Coordinate(r, c);
            direction = Direction.values()[rng.nextInt(4)];
            // must be non-edge
            if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                continue;
            }
            // ensure it doesn't point into a wall
            switch (direction) {
                case UP    -> { if (r <= 1) continue; }
                case DOWN  -> { if (r >= rows - 2) continue; }
                case LEFT  -> { if (c <= 1) continue; }
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
            int c = !clampRow
                  ? (rng.nextBoolean() ? cols - 1 : 0)
                  : rng.nextInt(cols - 2) + 1;
            // avoid source location
            coord = new Coordinate(r, c);
            if (coord.equals(sourceCell.coord)) {
                continue;
            }
            if (clampRow) {
                direction = (r == 0 ? Direction.UP : Direction.DOWN);
            } else {
                direction = (c == 0 ? Direction.LEFT : Direction.RIGHT);
            }
            break;
        } while (true);
        return new TerminationCell.CreateInfo(coord, direction);
    }

    /**
     * Displays the current map.
     */
    public void display() {
        int pad = String.valueOf(rows - 1).length();
        Runnable printCols = () -> {
            System.out.print(StringUtils.createPadding(pad, ' '));
            System.out.print(' ');
            for (int i = 0; i < cols - 2; i++) {
                System.out.print((char)('A' + i));
            }
            System.out.println();
        };
        printCols.run();
        for (int r = 0; r < rows; r++) {
            if (r != 0 && r != rows - 1) {
                System.out.print(String.format("%" + pad + "s", r));
            } else {
                System.out.print(StringUtils.createPadding(pad, ' '));
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
     * Undoes the last placed pipe at coord.
     */
    public void undo(@NotNull Coordinate coord) {
        Cell cell = cells[coord.row][coord.col];
        if (!(cell instanceof FillableCell fillable)) {
            throw new IllegalArgumentException("Cannot undo non-fillable cell at " + coord);
        }
        fillable.resetPipe();
        filledTiles.remove(coord);
    }

    /**
     * Marks the source tile as filled.
     */
    public void fillBeginTile() {
        sourceCell.setFilled();
    }

    @NotNull
    private List<Coordinate> getTraversedCoords() {
        return new ArrayList<>(filledTiles);
    }

    /**
     * Fills all pipes within {@code distance} from the source.
     */
    public void fillTiles(int distance) {
        if (prevFilledDistance != null && distance < prevFilledDistance) {
            filledTiles.clear();
            prevFilledTiles = 0;
        }
        prevFilledDistance = distance;

        if (filledTiles.isEmpty()) {
            filledTiles.add(sourceCell.coord);
            sourceCell.setFilled();
        }

        Queue<Coordinate> queue = new ArrayDeque<>(getTraversedCoords());
        Set<Coordinate> visited = new HashSet<>(filledTiles);
        int steps = 0;

        while (!queue.isEmpty() && steps < distance) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                Coordinate cur = queue.poll();
                Cell cc = cells[cur.row][cur.col];
                List<Direction> outs = new ArrayList<>();

                if (cc instanceof FillableCell fc && fc.getPipe().isPresent()) {
                    outs.addAll(Arrays.asList(fc.getPipe().get().getConnections()));
                } else if (cc instanceof TerminationCell tc) {
                    outs.add(tc.pointingTo);
                }

                for (Direction dir : outs) {
                    Coordinate nxt = cur.add(dir.getOffset());
                    if (nxt.row < 0 || nxt.row >= rows || nxt.col < 0 || nxt.col >= cols) {
                        continue;
                    }
                    Cell nc = cells[nxt.row][nxt.col];

                    if (nc instanceof FillableCell fc2 && fc2.getPipe().isPresent()) {
                        Pipe nextPipe = fc2.getPipe().get();
                        Direction back = dir.getOpposite();
                        if (Arrays.asList(nextPipe.getConnections()).contains(back)
                                && visited.add(nxt)) {
                            queue.add(nxt);
                            filledTiles.add(nxt);
                            nextPipe.setFilled();
                        }

                    } else if (nc instanceof TerminationCell tc2
                            && tc2.type == TerminationCell.Type.SINK
                            && dir == tc2.pointingTo.getOpposite()) {
                        filledTiles.add(nxt);
                        tc2.setFilled();
                    }
                }
            }
            steps++;
        }
        prevFilledTiles = filledTiles.size();
    }

    /**
     * Checks whether a filled path exists from source to sink.
     */
    public boolean checkPath() {
        Deque<Coordinate> queue = new ArrayDeque<>();
        Set<Coordinate> seen = new HashSet<>();
        queue.add(sourceCell.coord);
        seen.add(sourceCell.coord);

        while (!queue.isEmpty()) {
            Coordinate cur = queue.poll();
            Cell cc = cells[cur.row][cur.col];
            List<Direction> outs = new ArrayList<>();

            if (cc instanceof FillableCell fc && fc.getPipe().isPresent()) {
                Pipe p = fc.getPipe().get();
                if (!p.getFilled()) {
                    continue;
                }
                outs.addAll(Arrays.asList(p.getConnections()));

            } else if (cc instanceof TerminationCell tc) {
                if (tc.type == TerminationCell.Type.SOURCE) {
                    outs.add(tc.pointingTo);
                } else {
                    // Reached sink if its arrow is rendered as filled
                    char ch = tc.toSingleChar();
                    return (ch == PipePatterns.Filled.UP_ARROW
                         || ch == PipePatterns.Filled.DOWN_ARROW
                         || ch == PipePatterns.Filled.LEFT_ARROW
                         || ch == PipePatterns.Filled.RIGHT_ARROW);
                }
            }

            for (Direction d : outs) {
                Coordinate nxt = cur.add(d.getOffset());
                if (nxt.row < 0 || nxt.row >= rows || nxt.col < 0 || nxt.col >= cols) {
                    continue;
                }
                if (seen.add(nxt)) {
                    queue.add(nxt);
                }
            }
        }
        return false;
    }

    /**
     * @return true if no new tiles were filled in the last round.
     */
    public boolean hasLost() {
        return prevFilledDistance != null && filledTiles.size() == prevFilledTiles;
    }
}