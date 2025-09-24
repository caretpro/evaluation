
public Map(int rows, int cols) {
    if (rows < 3 || cols < 3) {
        throw new IllegalArgumentException("Map size must be at least 3x3.");
    }
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
    TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
    Coordinate sourceCoord = sourceInfo.coord;
    Direction sourceDir = sourceInfo.dir;
    sourceCell = new TerminationCell(sourceCoord, sourceDir, TerminationCell.Type.SOURCE);
    cells[sourceCoord.row][sourceCoord.col] = sourceCell;
    TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
    Coordinate sinkCoord = sinkInfo.coord;
    Direction sinkDir = sinkInfo.dir;
    sinkCell = new TerminationCell(sinkCoord, sinkDir, TerminationCell.Type.SINK);
    cells[sinkCoord.row][sinkCoord.col] = sinkCell;
}

public Map(int rows, int cols, Cell[][] cells) {
    if (rows < 3 || cols < 3) {
        throw new IllegalArgumentException("Map size must be at least 3x3.");
    }
    if (cells == null || cells.length != rows) {
        throw new IllegalArgumentException("Cells array row count mismatch.");
    }
    for (int r = 0; r < rows; r++) {
        if (cells[r] == null || cells[r].length != cols) {
            throw new IllegalArgumentException("Cells array column count mismatch at row " + r);
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
                boolean isEdge = (r == 0 || r == rows - 1 || c == 0 || c == cols - 1);
                if (tc.type == TerminationCell.Type.SOURCE) {
                    if (isEdge) {
                        throw new IllegalArgumentException(
                                "Source cell cannot be on edge at (" + r + "," + c + ").");
                    }
                    if (foundSource != null) {
                        throw new IllegalArgumentException("Multiple source cells found.");
                    }
                    Coordinate adjacent = tc.coord.add(tc.pointingTo.getOffset());
                    if (!isWithinBounds(adjacent)) {
                        throw new IllegalArgumentException(
                                "Source cell points outside map at (" + r + "," + c + ").");
                    }
                    Cell adjacentCell = cells[adjacent.row][adjacent.col];
                    if (adjacentCell instanceof Wall) {
                        throw new IllegalArgumentException(
                                "Source cell points into a wall at (" + r + "," + c + ").");
                    }
                    foundSource = tc;
                } else if (tc.type == TerminationCell.Type.SINK) {
                    if (!isEdge) {
                        throw new IllegalArgumentException("Sink cell must be on edge at (" + r + "," + c + ").");
                    }
                    if (foundSink != null) {
                        throw new IllegalArgumentException("Multiple sink cells found.");
                    }
                    Coordinate adjacent = tc.coord.add(tc.pointingTo.getOffset());
                    if (isWithinBounds(adjacent)) {
                        throw new IllegalArgumentException(
                                "Sink cell must point outside the map at (" + r + "," + c + ").");
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