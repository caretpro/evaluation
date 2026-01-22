
package pa1.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A rectangular grid of {@link Cell}s containing {@link EntityCell}s.
 * Also tracks gem locations and enforces that all gems are reachable from the stop cell.
 */
public class GameBoard {

    private final int numRows;
    private final int numCols;
    private final Cell[][] cells;
    private final EntityCell[][] entityCells;
    private final StopCell stopCell;
    private final List<EntityCell> gemCells;

    /**
     * Builds a game board from a list of strings (one per row), using characters:
     * <ul>
     *   <li>'.' → StopCell (exactly one allowed)</li>
     *   <li>'#' → Wall</li>
     *   <li>'G' → Gem EntityCell</li>
     *   <li>'M' → Mine EntityCell</li>
     *   <li>'L' → ExtraLife EntityCell</li>
     *   <li>'E' → UnlimitedLives EntityCell</li>
     *   <li>'P' → Player EntityCell</li>
     * </ul>
     *
     * @param rows the ASCII rows
     * @throws IllegalArgumentException if the layout is invalid or some gem is unreachable
     */
    public GameBoard(@NotNull final List<String> rows) {
        Objects.requireNonNull(rows, "rows");
        this.numRows = rows.size();
        if (numRows == 0) {
            throw new IllegalArgumentException("Must have at least one row");
        }
        this.numCols = rows.get(0).length();
        if (numCols == 0) {
            throw new IllegalArgumentException("Must have at least one column");
        }

        // Allocate arrays
        this.cells = new Cell[numRows][numCols];
        this.entityCells = new EntityCell[numRows][numCols];
        this.gemCells = new ArrayList<>();

        StopCell foundStop = null;

        for (int r = 0; r < numRows; r++) {
            String row = rows.get(r);
            if (row.length() != numCols) {
                throw new IllegalArgumentException("All rows must have same length");
            }
            for (int c = 0; c < numCols; c++) {
                char ch = row.charAt(c);
                Position pos = new Position(r, c);

                Cell base;
                switch (ch) {
                    case '.':
                        base = new StopCell(pos);
                        if (foundStop != null) {
                            throw new IllegalArgumentException("Multiple stop cells");
                        }
                        foundStop = (StopCell) base;
                        break;
                    case '#':
                        base = new WallCell(pos);
                        break;
                    default:
                        base = new StopCell(pos); // placeholder until we wrap it in EntityCell
                }

                cells[r][c] = base;

                // Wrap in EntityCell if ch is an entity type:
                Entity initial = switch (ch) {
                    case 'G' -> new Gem();
                    case 'M' -> new Mine();
                    case 'L' -> new ExtraLife();
                    case 'E' -> new UnlimitedLives();
                    case 'P' -> new PlayerEntity();
                    default -> null;
                };
                if (initial != null) {
                    EntityCell ec = new EntityCell(pos, initial);
                    entityCells[r][c] = ec;
                    cells[r][c] = ec;
                    if (initial instanceof Gem) {
                        gemCells.add(ec);
                    }
                }
            }
        }

        if (foundStop == null) {
            throw new IllegalArgumentException("Must have exactly one stop cell");
        }
        this.stopCell = foundStop;

        // Now that we've collected all gem‐cells, enforce reachability:
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("Some gems are unreachable from stop cell");
        }
    }

    /** Checks that every gem cell is reachable from the stop cell by flood‐fill over EntityCell neighbors. */
    private boolean isAllGemsReachable() {
        Set<EntityCell> visited = new HashSet<>();
        Deque<EntityCell> frontier = new ArrayDeque<>();
        // The stopCell must itself be an EntityCell to start traversal:
        if (!(stopCell instanceof EntityCell stopEnt)) {
            // no traversal possible, but if there are no gems it's OK:
            return gemCells.isEmpty();
        }
        frontier.add(stopEnt);
        visited.add(stopEnt);

        while (!frontier.isEmpty()) {
            EntityCell curr = frontier.removeFirst();
            for (Cell nbr : neighbors(curr.getPosition())) {
                if (nbr instanceof EntityCell ec && !visited.contains(ec)) {
                    visited.add(ec);
                    frontier.add(ec);
                }
            }
        }
        return visited.containsAll(gemCells);
    }

    private List<Cell> neighbors(Position p) {
        int r = p.getRow(), c = p.getCol();
        List<Cell> out = new ArrayList<>(4);
        if (r > 0) out.add(cells[r - 1][c]);
        if (r + 1 < numRows) out.add(cells[r + 1][c]);
        if (c > 0) out.add(cells[r][c - 1]);
        if (c + 1 < numCols) out.add(cells[r][c + 1]);
        return out;
    }

    /** @return the number of rows. */
    public int getNumRows() {
        return numRows;
    }

    /** @return the number of columns. */
    public int getNumCols() {
        return numCols;
    }

    /** @return the stop cell. */
    @NotNull
    public StopCell getStopCell() {
        return stopCell;
    }

    /** @return the cell at (row,col). */
    @NotNull
    public Cell getCell(int row, int col) {
        return Objects.requireNonNull(cells[row][col]);
    }

    /** @return the cell at the given position. */
    @NotNull
    public Cell getCell(@NotNull Position pos) {
        Objects.requireNonNull(pos);
        return getCell(pos.getRow(), pos.getCol());
    }

    /** @return the EntityCell at (row,col), or null if it's not an EntityCell. */
    public EntityCell getEntityCell(int row, int col) {
        return entityCells[row][col];
    }

    /** @return the EntityCell at the given Position, or null if it's not an EntityCell. */
    public EntityCell getEntityCell(@NotNull Position pos) {
        Objects.requireNonNull(pos);
        return getEntityCell(pos.getRow(), pos.getCol());
    }

    /** @return all EntityCells in the grid. */
    public List<EntityCell> getAllEntityCells() {
        return Arrays.stream(entityCells)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /** @return number of gems currently remaining on the board. */
    public int getNumGems() {
        return (int) getAllEntityCells().stream().filter(ec -> ec.getEntity() instanceof Gem).count();
    }

    // ... you may have other methods here (e.g., for serialization) ...
}