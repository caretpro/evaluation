
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

    // ... other methods and fields ...

    // Correct constructor without return type
    public Map(int rows, int cols) {
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map must be at least 3x3 in size.");
        }
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Wall(new Coordinate(r, c));
            }
        }
        for (int r = 1; r < rows - 1; r++) {
            for (int c = 1; c < cols - 1; c++) {
                cells[r][c] = new FillableCell(new Coordinate(r, c));
            }
        }
        TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
        Coordinate sourceCoord = sourceInfo.coord;
        Direction sourceDir = sourceInfo.direction;
        sourceCell = new TerminationCell(sourceCoord, sourceDir, true);
        cells[sourceCoord.row][sourceCoord.col] = sourceCell;
        TerminationCell.CreateInfo sinkInfo;
        do {
            sinkInfo = generateEndCellInfo();
        } while (sinkInfo.coord.equals(sourceCoord));
        Coordinate sinkCoord = sinkInfo.coord;
        Direction sinkDir = sinkInfo.direction;
        sinkCell = new TerminationCell(sinkCoord, sinkDir, false);
        cells[sinkCoord.row][sinkCoord.col] = sinkCell;
    }

    // Correct constructor without return type
    public Map(int rows, int cols, Cell[][] cells) {
        if (rows < 3 || cols < 3) {
            throw new IllegalArgumentException("Map must be at least 3x3 in size.");
        }
        if (cells == null || cells.length != rows) {
            throw new IllegalArgumentException("Cells array size mismatch.");
        }
        for (Cell[] row : cells) {
            if (row == null || row.length != cols) {
                throw new IllegalArgumentException("Cells array size mismatch.");
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
                if (cell == null) {
                    throw new IllegalArgumentException("Cell at (" + r + "," + c + ") is null.");
                }
                this.cells[r][c] = cell;
                if (cell instanceof TerminationCell tc) {
                    Coordinate coord = new Coordinate(r, c);
                    boolean isEdge = (r == 0 || r == rows - 1 || c == 0 || c == cols - 1);
                    if (tc.isSource()) {
                        if (isEdge) {
                            throw new IllegalArgumentException(
                                    "Source cell must not be on edge at (" + r + "," + c + ").");
                        }
                        if (foundSource != null) {
                            throw new IllegalArgumentException("Multiple source cells found.");
                        }
                        Coordinate adjacent = coord.add(tc.getDirection().getOffset());
                        if (!isInBounds(adjacent)) {
                            throw new IllegalArgumentException("Source cell direction points outside the map.");
                        }
                        Cell adjacentCell = cells[adjacent.row][adjacent.col];
                        if (adjacentCell instanceof Wall) {
                            throw new IllegalArgumentException("Source cell direction points into a wall at ("
                                    + adjacent.row + "," + adjacent.col + ").");
                        }
                        foundSource = tc;
                    } else {
                        if (!isEdge) {
                            throw new IllegalArgumentException("Sink cell must be on edge at (" + r + "," + c + ").");
                        }
                        if (foundSink != null) {
                            throw new IllegalArgumentException("Multiple sink cells found.");
                        }
                        Coordinate adjacent = coord.add(tc.getDirection().getOffset());
                        if (isInBounds(adjacent)) {
                            throw new IllegalArgumentException("Sink cell direction must point outside the map.");
                        }
                        foundSink = tc;
                    }
                }
            }
        }
        if (foundSource == null) {
            throw new IllegalArgumentException("No source cell found.");
        }
        if (foundSink == null) {
            throw new IllegalArgumentException("No sink cell found.");
        }
        this.sourceCell = foundSource;
        this.sinkCell = foundSink;
    }

    // ... rest of the class ...
}