
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

	// ... other methods ...

	/**
	 * Proper constructor for Map with rows and cols.
	 */
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

	/**
	 * Proper constructor for Map with rows, cols and predefined cells.
	 */
	public Map(int rows, int cols, Cell[][] cells) {
		if (rows < 3 || cols < 3) {
			throw new IllegalArgumentException("Map size must be at least 3x3.");
		}
		if (cells == null || cells.length != rows) {
			throw new IllegalArgumentException("Cells array must match the specified rows.");
		}
		for (Cell[] row : cells) {
			if (row == null || row.length != cols) {
				throw new IllegalArgumentException("Cells array must match the specified columns.");
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
					if (tc.isSource()) {
						if (isEdge) {
							throw new IllegalArgumentException(
									"Source cell cannot be on the edge at (" + r + "," + c + ").");
						}
						if (foundSource != null) {
							throw new IllegalArgumentException("Multiple source cells found.");
						}
						Coordinate adj = new Coordinate(r, c).add(tc.getDirection().getOffset());
						if (!isValidCoordinate(adj) || this.cells[adj.row][adj.col] instanceof Wall) {
							throw new IllegalArgumentException(
									"Source cell points into a wall or outside map at (" + r + "," + c + ").");
						}
						foundSource = tc;
					} else {
						if (!isEdge) {
							throw new IllegalArgumentException(
									"Sink cell must be on the edge at (" + r + "," + c + ").");
						}
						Coordinate adj = new Coordinate(r, c).add(tc.getDirection().getOffset());
						if (isValidCoordinate(adj)) {
							throw new IllegalArgumentException(
									"Sink cell must point outside the map at (" + r + "," + c + ").");
						}
						if (foundSink != null) {
							throw new IllegalArgumentException("Multiple sink cells found.");
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