
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

	/**
	 * Constructor with only size parameters, initializes cells with FillableCell.
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
		sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.direction);
		cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
		TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
		sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.direction);
		cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
	}

	/**
	 * Constructor with size and cell array.
	 */
	public Map(int rows, int cols, Cell[][] cells) {
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		for (int r = 0; r < rows; r++) {
			System.arraycopy(cells[r], 0, this.cells[r], 0, cols);
		}
		// Additional initialization if needed
	}

	// Existing methods...

}