
/**
 * Creates a map with size of rows x cols. <p> The map should only contain one source tile in any non-edge cell. The map should only contain one sink tile in any edge cell. The source tile must not point into a wall. The sink tile must point outside the map. </p>
 * @param rows  Number of rows.
 * @param cols  Number of columns.
 */
public Map(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.cells = new Cell[rows][cols];
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            cells[i][j] = new Wall(new Coordinate(i, j));
        }
    }
    TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
    sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.dir, TerminationCell.Type.SOURCE);
    cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
    TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
    sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.dir, TerminationCell.Type.SINK);
    cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
}

/**
 * Creates a map with the given cells. <p> The map should only contain one source tile in any non-edge cell. The map should only contain one sink tile in any edge cell. The source tile must not point into a wall. The sink tile must point outside the map. </p>
 * @param rows   Number of rows.
 * @param cols   Number of columns.
 * @param cells  Cells to fill the map.
 */
public Map(int rows, int cols, Cell[][] cells) {
    this.rows = rows;
    this.cols = cols;
    this.cells = cells;
    int sourceCount = 0;
    int sinkCount = 0;
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            Cell cell = cells[i][j];
            if (cell instanceof TerminationCell) {
                TerminationCell termCell = (TerminationCell) cell;
                if (termCell.type == TerminationCell.Type.SOURCE) {
                    sourceCount++;
                    sourceCell = termCell;
                    if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                        throw new IllegalArgumentException("Source must be in non-edge cell");
                    }
                    Coordinate adjacent = termCell.coord.add(termCell.pointingTo.getOffset());
                    if (adjacent.row < 0 || adjacent.row >= rows || adjacent.col < 0 || adjacent.col >= cols
                            || cells[adjacent.row][adjacent.col] instanceof Wall) {
                        throw new IllegalArgumentException("Source must not point into wall");
                    }
                } else if (termCell.type == TerminationCell.Type.SINK) {
                    sinkCount++;
                    sinkCell = termCell;
                    if (i != 0 && i != rows - 1 && j != 0 && j != cols - 1) {
                        throw new IllegalArgumentException("Sink must be in edge cell");
                    }
                    Coordinate outside = termCell.coord.add(termCell.pointingTo.getOffset());
                    if (outside.row >= 0 && outside.row < rows && outside.col >= 0 && outside.col < cols) {
                        throw new IllegalArgumentException("Sink must point outside map");
                    }
                }
            }
        }
    }
    if (sourceCount != 1) {
        throw new IllegalArgumentException("Map must contain exactly one source tile");
    }
    if (sinkCount != 1) {
        throw new IllegalArgumentException("Map must contain exactly one sink tile");
    }
}