
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
	 * Constructor to initialize map with given size.
	 */
	public Map(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				cells[r][c] = new FillableCell();
			}
		}
		TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
		Coordinate sourceCoord = sourceInfo.coord;
		Direction sourceDirection = sourceInfo.direction;
		sourceCell = new TerminationCell(sourceCoord, sourceDirection, true);
		cells[sourceCoord.row][sourceCoord.col] = sourceCell;
		TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
		Coordinate sinkCoord = sinkInfo.coord;
		Direction sinkDirection = sinkInfo.direction;
		sinkCell = new TerminationCell(sinkCoord, sinkDirection, false);
		cells[sinkCoord.row][sinkCoord.col] = sinkCell;
	}

	/**
	 * Constructor to initialize map with given size and cell array.
	 */
	public Map(int rows, int cols, @NotNull Cell[][] cells) {
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		for (int r = 0; r < rows; r++) {
			System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
		}
		sourceCell = null;
		sinkCell = null;
		int sourceCount = 0;
		int sinkCount = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Cell cell = this.cells[r][c];
				if (cell instanceof TerminationCell termCell) {
					if (termCell.isSource()) {
						sourceCount++;
						sourceCell = termCell;
						if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
							throw new IllegalArgumentException("Source must not be on edge");
						}
						Direction dir = termCell.getDirection();
						Coordinate nextCoord = new Coordinate(r, c).add(dir.getOffset());
						if (isInBounds(nextCoord)) {
							Cell nextCell = this.cells[nextCoord.row][nextCoord.col];
							if (nextCell instanceof Wall) {
								throw new IllegalArgumentException("Source cannot point into a wall");
							}
						}
					} else {
						if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
							sinkCount++;
							sinkCell = termCell;
							Direction dir = termCell.getDirection();
							Coordinate nextCoord = new Coordinate(r, c).add(dir.getOffset());
							if (isInBounds(nextCoord)) {
								throw new IllegalArgumentException("Sink must point outside the map");
							}
						}
					}
				}
			}
		}
		if (sourceCount != 1) {
			throw new IllegalArgumentException("There must be exactly one source cell");
		}
		if (sinkCount != 1) {
			throw new IllegalArgumentException("There must be exactly one sink cell");
		}
	}

	// ... rest of the class remains unchanged
}