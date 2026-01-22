
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
	Direction sourceDir = sourceInfo.direction;
	Coordinate sourceTarget = sourceCoord.add(sourceDir.getOffset());
	if (cells[sourceTarget.row][sourceTarget.col] instanceof Wall) {
		throw new IllegalStateException("Source cell points into a wall.");
	}
	sourceCell = new TerminationCell(sourceCoord, sourceDir, true);
	cells[sourceCoord.row][sourceCoord.col] = sourceCell;
	TerminationCell.CreateInfo sinkInfo;
	Coordinate sinkCoord;
	Direction sinkDir;
	do {
		sinkInfo = generateEndCellInfo();
		sinkCoord = sinkInfo.coord;
		sinkDir = sinkInfo.direction;
		Coordinate sinkTarget = sinkCoord.add(sinkDir.getOffset());
		boolean outside = sinkTarget.row < 0 || sinkTarget.row >= rows || sinkTarget.col < 0
				|| sinkTarget.col >= cols;
		if (outside && !sinkCoord.equals(sourceCoord)) {
			break;
		}
	} while (true);
	sinkCell = new TerminationCell(sinkCoord, sinkDir, false);
	cells[sinkCoord.row][sinkCoord.col] = sinkCell;
}

public Map(int rows, int cols, Cell[][] cells) {
	if (rows < 3 || cols < 3) {
		throw new IllegalArgumentException("Map size must be at least 3x3.");
	}
	if (cells == null || cells.length != rows) {
		throw new IllegalArgumentException("Cells array row count does not match rows.");
	}
	for (int r = 0; r < rows; r++) {
		if (cells[r] == null || cells[r].length != cols) {
			throw new IllegalArgumentException("Cells array column count does not match cols at row " + r);
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
				boolean isEdge = r == 0 || r == rows - 1 || c == 0 || c == cols - 1;
				if (tc.isSource()) {
					if (isEdge) {
						throw new IllegalArgumentException(
								"Source cell must not be on edge at (" + r + "," + c + ").");
					}
					if (foundSource != null) {
						throw new IllegalArgumentException("Multiple source cells found.");
					}
					Coordinate target = coord.add(tc.getDirection().getOffset());
					if (target.row < 0 || target.row >= rows || target.col < 0 || target.col >= cols) {
						throw new IllegalArgumentException("Source cell points outside the map.");
					}
					Cell targetCell = cells[target.row][target.col];
					if (targetCell instanceof Wall) {
						throw new IllegalArgumentException("Source cell points into a wall.");
					}
					foundSource = tc;
				} else {
					if (!isEdge) {
						throw new IllegalArgumentException("Sink cell must be on edge at (" + r + "," + c + ").");
					}
					if (foundSink != null) {
						throw new IllegalArgumentException("Multiple sink cells found.");
					}
					Coordinate target = coord.add(tc.getDirection().getOffset());
					boolean outside = target.row < 0 || target.row >= rows || target.col < 0 || target.col >= cols;
					if (!outside) {
						throw new IllegalArgumentException("Sink cell must point outside the map.");
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