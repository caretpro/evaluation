
public Map(int rows, int cols) {
	if (rows < 3 || cols < 3) {
		throw new IllegalArgumentException("Map size must be at least 3x3");
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

public Map(int rows, int cols, Cell[][] cells) {
	if (rows < 3 || cols < 3) {
		throw new IllegalArgumentException("Map size must be at least 3x3");
	}
	if (cells == null || cells.length != rows) {
		throw new IllegalArgumentException("Cells array row count mismatch");
	}
	for (Cell[] row : cells) {
		if (row == null || row.length != cols) {
			throw new IllegalArgumentException("Cells array column count mismatch");
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
				throw new IllegalArgumentException("Cell at (" + r + "," + c + ") is null");
			}
			this.cells[r][c] = cell;
			if (cell instanceof TerminationCell tc) {
				Coordinate coord = new Coordinate(r, c);
				boolean isEdge = (r == 0 || r == rows - 1 || c == 0 || c == cols - 1);
				if (tc.isSource()) {
					if (isEdge) {
						throw new IllegalArgumentException(
								"Source cell cannot be on edge at (" + r + "," + c + ")");
					}
					if (foundSource != null) {
						throw new IllegalArgumentException("Multiple source cells found");
					}
					Coordinate adjacent = coord.add(tc.getDirection().getOffset());
					if (!isValidCoordinate(adjacent) || this.cells[adjacent.row][adjacent.col] instanceof Wall) {
						throw new IllegalArgumentException(
								"Source cell points into a wall or outside map at (" + r + "," + c + ")");
					}
					foundSource = tc;
				} else {
					if (!isEdge) {
						throw new IllegalArgumentException("Sink cell must be on edge at (" + r + "," + c + ")");
					}
					Coordinate adjacent = coord.add(tc.getDirection().getOffset());
					if (isValidCoordinate(adjacent)) {
						throw new IllegalArgumentException(
								"Sink cell must point outside the map at (" + r + "," + c + ")");
					}
					if (foundSink != null) {
						throw new IllegalArgumentException("Multiple sink cells found");
					}
					foundSink = tc;
				}
			}
		}
	}
	if (foundSource == null) {
		throw new IllegalArgumentException("No source cell found");
	}
	if (foundSink == null) {
		throw new IllegalArgumentException("No sink cell found");
	}
	this.sourceCell = foundSource;
	this.sinkCell = foundSink;
}