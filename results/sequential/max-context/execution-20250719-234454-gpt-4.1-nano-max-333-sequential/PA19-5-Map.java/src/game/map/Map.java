
package game.map;

import game.map.cells.Cell;
import game.map.cells.FillableCell;
import game.map.cells.TerminationCell;
import game.map.cells.Wall;
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
	 * Constructs a map from a map string.
	 * <p>
	 * This is a convenience method for unit testing.
	 * </p>
	 *
	 * @param rows     Number of rows.
	 * @param cols     Number of columns.
	 * @param cellsRep String representation of the map, with columns delimited by {@code '\n'}.
	 * @return A map with the cells set from {@code cellsRep}.
	 * @throws IllegalArgumentException If the map is incorrectly formatted.
	 */
	@NotNull
	static Map fromString(int rows, int cols, @NotNull String cellsRep) {
		var cells = Deserializer.parseString(rows, cols, cellsRep);
		return new Map(rows, cols, cells);
	}

	/**
	 * Constructor for creating a new Map with specified rows and columns.
	 */
	public Map(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				this.cells[r][c] = new game.map.cells.EmptyCell(new Coordinate(r, c));
			}
		}
		TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
		this.sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.dir, TerminationCell.Type.SOURCE);
		this.cells[sourceInfo.coord.row][sourceInfo.coord.col] = this.sourceCell;
		TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
		this.sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.dir, TerminationCell.Type.SINK);
		this.cells[sinkInfo.coord.row][sinkInfo.coord.col] = this.sinkCell;
	}

	/**
	 * Constructor for creating a Map with specified rows, columns, and cell array.
	 */
	public Map(int rows, int cols, Cell[][] cells) {
		if (cells.length != rows) {
			throw new IllegalArgumentException("Cells array row count does not match specified rows");
		}
		for (Cell[] rowCells : cells) {
			if (rowCells.length != cols) {
				throw new IllegalArgumentException("Cells array column count does not match specified cols");
			}
		}
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		for (int r = 0; r < rows; r++) {
			System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
		}
		TerminationCell foundSource = null;
		TerminationCell foundSink = null;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Cell cell = this.cells[r][c];
				if (cell instanceof TerminationCell) {
					TerminationCell termCell = (TerminationCell) cell;
					if (termCell.type == TerminationCell.Type.SOURCE) {
						if (foundSource != null) {
							throw new IllegalArgumentException("Multiple source cells found");
						}
						if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
							throw new IllegalArgumentException("Source cell must not be on the edge");
						}
						Direction dir = termCell.pointingTo;
						Coordinate offset = dir.getOffset();
						int adjRow = r + offset.row;
						int adjCol = c + offset.col;
						if (adjRow >= 0 && adjRow < rows && adjCol >= 0 && adjCol < cols) {
							Cell adjacent = this.cells[adjRow][adjCol];
							if (adjacent instanceof Wall) {
								throw new IllegalArgumentException("Source points into a wall");
							}
						}
						foundSource = termCell;
					} else if (termCell.type == TerminationCell.Type.SINK) {
						if (foundSink != null) {
							throw new IllegalArgumentException("Multiple sink cells found");
						}
						if (!(r == 0 || r == rows - 1 || c == 0 || c == cols - 1)) {
							throw new IllegalArgumentException("Sink cell must be on the edge");
						}
						Direction dir = termCell.pointingTo;
						Coordinate offset = dir.getOffset();
						int adjRow = r + offset.row;
						int adjCol = c + offset.col;
						if (adjRow >= 0 && adjRow < rows && adjCol >= 0 && adjCol < cols) {
							throw new IllegalArgumentException("Sink must point outside the map");
						}
						foundSink = termCell;
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
}