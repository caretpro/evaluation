
package pa1.model;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;

/**
 * A game board: a rectangular grid of {@link EntityCell}s (which may contain {@link Entity}s) or {@link StopCell}s.
 * It holds exactly one start cell, zero or more gem cells, zero or more extra-life and mine cells, and zero or more
 * stop cells.
 */
public final class GameBoard implements BoardElement {

    private final int numRows;
    private final int numCols;
    private final EntityCell[][] entityCells; // grid of entity‑capable terrain
    private final StopCell[][] stopCells;     // grid of stop vs. pass‑through cells; exactly one is the start

    private final List<EntityCell> gemCells = new ArrayList<>();

    /**
     * Construct full game board from a textual grid.
     *
     * @param layout  A rectangular array of strings describing each cell's content:
     *                'S' = start/stop cell; 'G' = gem cell; 'M' = mine cell; 'E' = extra life cell; '.' = pass‑through.
     * @param initialLives initial number of lives (<=0 for unlimited)
     * @throws IllegalArgumentException if layout is empty, ragged, or any gem is not reachable from the start
     */
    public GameBoard(@NotNull final String[][] layout, final int initialLives) {
        Objects.requireNonNull(layout, "layout must not be null");
        if (layout.length == 0 || layout[0].length == 0) {
            throw new IllegalArgumentException("layout must have positive dimensions");
        }
        this.numRows = layout.length;
        this.numCols = layout[0].length;
        // verify rectangular
        for (String[] row : layout) {
            if (row.length != numCols) {
                throw new IllegalArgumentException("layout must be rectangular");
            }
        }

        // 1) Allocate grids
        this.entityCells = new EntityCell[numRows][numCols];
        this.stopCells   = new StopCell[numRows][numCols];

        // 2) Populate raw cells
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                String tok = layout[r][c];
                switch (tok) {
                    case "S":
                        stopCells[r][c]   = new StopCell();
                        entityCells[r][c] = stopCells[r][c];
                        break;
                    case "G":
                        EntityCell gem = new GemCell();
                        stopCells[r][c]   = new StopCell();
                        entityCells[r][c] = gem;
                        gemCells.add(gem);
                        break;
                    case "M":
                        MineCell mine = new MineCell();
                        stopCells[r][c]   = new StopCell();
                        entityCells[r][c] = mine;
                        break;
                    case "E":
                        ExtraLifeCell extra = new ExtraLifeCell();
                        stopCells[r][c]      = new StopCell();
                        entityCells[r][c]    = extra;
                        break;
                    case ".":
                        pass:
                        {
                            // pass‑through cell: no stopping, but entities pass over it
                            // so it's still an EntityCell but no StopCell
                            entityCells[r][c] = new EntityCell();
                            stopCells[r][c]   = null;
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("unrecognized cell type '" + tok + "'");
                }
            }
        }

        // 3) Wire up neighbors
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                EntityCell ec = entityCells[r][c];
                if (ec != null) {
                    ec.setNeighbors(
                        r > 0    ? entityCells[r - 1][c] : null,
                        r + 1 < numRows ? entityCells[r + 1][c] : null,
                        c > 0    ? entityCells[r][c - 1] : null,
                        c + 1 < numCols  ? entityCells[r][c + 1] : null
                    );
                }
                StopCell sc = stopCells[r][c];
                if (sc != null) {
                    sc.setPosition(r, c);
                }
            }
        }

        // 4) Validate gem reachability _after_ grid is fully built
        if (!isAllGemsReachable()) {
            throw new IllegalArgumentException("not all gems are reachable from the start");
        }
    }

    /**
     * Returns {@code true} iff every gem cell is reachable (via EntityCell adjacency) from the start cell.
     */
    private boolean isAllGemsReachable() {
        // find the start StopCell
        EntityCell start = null;
        outer:
        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                StopCell sc = stopCells[r][c];
                if (sc != null && sc.isStart()) {
                    start = Objects.requireNonNull(entityCells[r][c], "start cell must also be an EntityCell");
                    break outer;
                }
            }
        }
        Objects.requireNonNull(start, "no start cell found");

        // BFS from start over EntityCell graph
        Deque<EntityCell> queue = new ArrayDeque<>();
        Set<EntityCell> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            EntityCell cell = queue.removeFirst();
            for (EntityCell nbr : cell.getNeighbors()) {
                if (nbr != null && visited.add(nbr)) {
                    queue.add(nbr);
                }
            }
        }

        // ensure all gems in gemCells are in visited
        return gemCells.stream().allMatch(visited::contains);
    }

    // ... other BoardElement methods, getters, etc. ...
}