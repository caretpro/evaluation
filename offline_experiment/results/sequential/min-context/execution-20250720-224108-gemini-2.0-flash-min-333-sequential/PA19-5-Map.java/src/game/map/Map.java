
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
	 * Tries to place a pipe at (row, col).
	 *
	 * @param coord Coordinate to place pipe at.
	 * @param pipe  Pipe to place in cell.
	 * @return {@code true} if the pipe is placed in the cell, {@code false} otherwise.
	 */
	public boolean tryPlacePipe(@NotNull final Coordinate coord, @NotNull final Pipe pipe) {
		return tryPlacePipe(coord.row, coord.col, pipe);
	}

	@NotNull
	private TerminationCell.CreateInfo generateStartCellInfo() {
		Random rng = new Random();

		Coordinate coord;
		Direction direction;

		do {
			int row = rng.nextInt(rows);
			int col = rng.nextInt(cols);
			coord = new Coordinate(row, col);

			int dir = rng.nextInt(4);
			direction = Direction.values()[dir];

			if (row == 0 || row == rows - 1) {
				continue;
			}
			if (col == 0 || col == cols - 1) {
				continue;
			}
			switch (direction) {
			case UP:
				if (row <= 1) {
					continue;
				}
				break;
			case DOWN:
				if (row >= rows - 2) {
					continue;
				}
				break;
			case LEFT:
				if (col <= 1) {
					continue;
				}
				break;
			case RIGHT:
				if (col >= cols - 2) {
					continue;
				}
				break;
			}

			break;
		} while (true);

		return new TerminationCell.CreateInfo(coord, direction);
	}

	@NotNull
	private TerminationCell.CreateInfo generateEndCellInfo() {
		Random rng = new Random();

		Coordinate coord;
		Direction direction;

		do {
			// false -> X-axis, true -> Y-axis
			boolean axisToClamp = rng.nextInt(2) == 1;
			int row = axisToClamp ? (rng.nextInt(2) == 1 ? rows - 1 : 0) : rng.nextInt(rows - 2) + 1;
			int col = !axisToClamp ? (rng.nextInt(2) == 1 ? cols - 1 : 0) : rng.nextInt(cols - 2) + 1;

			if (row == col) {
				continue;
			}

			coord = new Coordinate(row, col);

			if (axisToClamp) {
				if (row == 0) {
					direction = Direction.UP;
				} else {
					direction = Direction.DOWN;
				}
			} else {
				if (col == 0) {
					direction = Direction.LEFT;
				} else {
					direction = Direction.RIGHT;
				}
			}

			var adjacentCell = coord.add(direction.getOpposite().getOffset());
			if (adjacentCell.equals(sourceCell.coord)) {
				continue;
			}

			break;
		} while (true);

		return new TerminationCell.CreateInfo(coord, direction);
	}

	/**
	 * Displays the current map.
	 */
	public void display() {
		final int padLength = Integer.valueOf(rows - 1).toString().length();

		Runnable printColumns = () -> {
			System.out.print(StringUtils.createPadding(padLength, ' '));
			System.out.print(' ');
			for (int i = 0; i < cols - 2; ++i) {
				System.out.print((char) ('A' + i));
			}
			System.out.println();
		};

		printColumns.run();

		for (int i = 0; i < rows; ++i) {
			if (i != 0 && i != rows - 1) {
				System.out.print(String.format("%1$" + padLength + "s", i));
			} else {
				System.out.print(StringUtils.createPadding(padLength, ' '));
			}

			Arrays.stream(cells[i]).forEachOrdered(elem -> System.out.print(elem.toSingleChar()));

			if (i != 0 && i != rows - 1) {
				System.out.print(i);
			}

			System.out.println();
		}

		printColumns.run();
	}

	public void fillBeginTile() {
		sourceCell.setFilled();
	}

	@NotNull
	private List<Coordinate> getTraversedCoords() {
		return new ArrayList<>(filledTiles);
	}

	/**
	 * <p>
	 * Hint: The game is lost when a round ends and no pipes are filled during the round. Is
	 * there a way to check whether pipes are filled during a round?
	 * </p>
	 *
	 * @return {@code true} if the game is lost.
	 */
	public boolean hasLost() {
		if (filledTiles.size() == prevFilledTiles && prevFilledDistance != null) {
			return true;
		}
		return false;
	}

	public Map(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				this.cells[i][j] = new FillableCell(new Coordinate(i, j));
			}
		}
		TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
		TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
		this.sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.direction);
		this.sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.direction);
		this.cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
		this.cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
					if (!(cells[i][j] instanceof TerminationCell)) {
						cells[i][j] = new Wall(new Coordinate(i, j));
					}
				}
			}
		}
	}

	public Map(int rows, int cols, Cell[][] cells) {
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		int sourceCount = 0;
		int sinkCount = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				this.cells[i][j] = cells[i][j];
				if (cells[i][j] instanceof TerminationCell) {
					TerminationCell terminationCell = (TerminationCell) cells[i][j];
					if (i > 0 && i < rows - 1 && j > 0 && j < cols - 1) {
						if (sourceCount > 0) {
							throw new IllegalArgumentException(
									"Map should only contain one source tile in any non-edge cell.");
						}
						sourceCount++;
						this.sourceCell = terminationCell;
						Coordinate target = terminationCell.coord.add(terminationCell.getDirection().getOffset());
						if (target.row < 0 || target.row >= rows || target.col < 0 || target.col >= cols
								|| this.cells[target.row][target.col] instanceof Wall) {
							throw new IllegalArgumentException("The source tile must not point into a wall.");
						}
					} else {
						if (sinkCount > 0) {
							throw new IllegalArgumentException(
									"Map should only contain one sink tile in any edge cell.");
						}
						sinkCount++;
						this.sinkCell = terminationCell;
						Coordinate target = terminationCell.coord.add(terminationCell.getDirection().getOffset());
						if (target.row >= 0 && target.row < rows && target.col >= 0 && target.col < cols) {
							throw new IllegalArgumentException("The sink tile must point outside the map.");
						}
					}
				}
			}
		}
	}

	/**
	 * Tries to place a pipe at (row, col). <p> Note: You cannot overwrite the pipe of a cell once the cell is occupied. </p> <p> Hint: Remember to check whether the map coordinates are within bounds, and whether the target cell is a  {@link FillableCell} . </p>
	 * @param row  One-Based row number to place pipe at.
	 * @param col  One-Based column number to place pipe at.
	 * @param p    Pipe to place in cell.
	 * @return   {@code  true}  if the pipe is placed in the cell,  {@code  false}  otherwise.
	 */
	boolean tryPlacePipe(int row, int col, Pipe p) {
		if (row <= 0 || row > rows || col <= 0 || col > cols) {
			return false;
		}
		Cell cell = cells[row - 1][col - 1];
		if (!(cell instanceof FillableCell)) {
			return false;
		}
		FillableCell fillableCell = (FillableCell) cell;
		if (fillableCell.hasPipe()) {
			return false;
		}
		fillableCell.setPipe(p);
		return true;
	}

	/**
	 * Undoes a step from the map. <p> Effectively replaces the cell with an empty cell in the coordinate specified. </p>
	 * @param coord  Coordinate to reset.
	 * @throws IllegalArgumentException  if the cell is not an instance of  {@link FillableCell} .
	 */
	public void undo(final Coordinate coord) {
		if (coord.row < 0 || coord.row >= rows || coord.col < 0 || coord.col >= cols) {
			throw new IllegalArgumentException("Coordinate out of bounds.");
		}
		Cell cell = cells[coord.row][coord.col];
		if (!(cell instanceof FillableCell)) {
			throw new IllegalArgumentException("Cell is not an instance of FillableCell.");
		}
		FillableCell fillableCell = (FillableCell) cell;
		fillableCell.removePipe();
	}

	/**
	 * Fills all pipes that are within  {@code  distance}  units from the  {@code  sourceCell} . <p> Hint: There are two ways to approach this. You can either iteratively fill the tiles by distance (i.e. filling  distance=0, distance=1, etc), or you can save the tiles you have already filled, and fill all adjacent cells of  the already-filled tiles. Whichever method you choose is up to you, as long as the result is the same. </p>
	 * @param distance  Distance to fill pipes.
	 */
	public void fillTiles(int distance) {
		if (distance < 0) {
			return;
		}
		if (prevFilledDistance != null && distance <= prevFilledDistance && filledTiles.size() == prevFilledTiles) {
			return;
		}
		Queue<Coordinate> queue = new LinkedList<>();
		Set<Coordinate> visited = new HashSet<>();
		queue.offer(sourceCell.coord);
		visited.add(sourceCell.coord);
		filledTiles.add(sourceCell.coord);
		for (int d = 0; d < distance; d++) {
			int size = queue.size();
			for (int i = 0; i < size; i++) {
				Coordinate current = queue.poll();
				for (Direction dir : Direction.values()) {
					Coordinate neighborCoord = current.add(dir.getOffset());
					if (neighborCoord.row >= 0 && neighborCoord.row < rows && neighborCoord.col >= 0
							&& neighborCoord.col < cols) {
						Cell neighborCell = cells[neighborCoord.row][neighborCoord.col];
						if (neighborCell instanceof FillableCell && !visited.contains(neighborCoord)) {
							FillableCell fillableCell = (FillableCell) neighborCell;
							if (fillableCell.hasPipe()) {
								queue.offer(neighborCoord);
								visited.add(neighborCoord);
								filledTiles.add(neighborCoord);
								fillableCell.setFilled();
							}
						}
					}
				}
			}
		}
		prevFilledTiles = filledTiles.size();
		prevFilledDistance = distance;
	}

	/**
	 * Checks whether there exists a path from  {@code  sourceCell}  to  {@code  sinkCell} . <p> The game is won when the player must place pipes on the map such that a path is formed from the source tile to the sink tile. One of the approaches to check this is to use Breadth First Search to search for the sink tile along the pipes. You may also use other algorithms or create your own, provided it achieves the same goal. The game is lost when no additional pipes are filled in each round after the Nth round. The value of N' can be configured in the loaded map. In the example maps, N` is set to 10. </p>
	 * @return   {@code  true}  if a path exists, else  {@code  false} .
	 */
	public boolean checkPath() {
		Queue<Coordinate> queue = new LinkedList<>();
		Set<Coordinate> visited = new HashSet<>();
		queue.offer(sourceCell.coord);
		visited.add(sourceCell.coord);
		while (!queue.isEmpty()) {
			Coordinate current = queue.poll();
			if (current.equals(sinkCell.coord)) {
				return true;
			}
			Cell currentCell = cells[current.row][current.col];
			for (Direction dir : Direction.values()) {
				Coordinate neighborCoord = current.add(dir.getOffset());
				if (neighborCoord.row >= 0 && neighborCoord.row < rows && neighborCoord.col >= 0
						&& neighborCoord.col < cols) {
					Cell neighborCell = cells[neighborCoord.row][neighborCoord.col];
					if (neighborCell instanceof FillableCell) {
						FillableCell fillableCell = (FillableCell) neighborCell;
						if (fillableCell.hasPipe() && !visited.contains(neighborCoord)) {
							queue.offer(neighborCoord);
							visited.add(neighborCoord);
						}
					}
				}
			}
		}
		return false;
	}
}