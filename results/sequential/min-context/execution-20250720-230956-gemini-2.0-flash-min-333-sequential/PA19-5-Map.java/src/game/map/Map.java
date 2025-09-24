
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
		if (filledTiles.size() == prevFilledTiles) {
			return true;
		}
		prevFilledTiles = filledTiles.size();
		return false;
	}

	public Map(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				cells[i][j] = new FillableCell(new Coordinate(i, j));
			}
		}
		TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
		TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
		this.sourceCell = new TerminationCell(sourceInfo.coord, sourceInfo.direction);
		this.sinkCell = new TerminationCell(sinkInfo.coord, sinkInfo.direction);
		cells[sourceInfo.coord.row][sourceInfo.coord.col] = sourceCell;
		cells[sinkInfo.coord.row][sinkInfo.coord.col] = sinkCell;
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
		this.cells = cells;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (cells[i][j] instanceof TerminationCell) {
					TerminationCell terminationCell = (TerminationCell) cells[i][j];
					if (i > 0 && i < rows - 1 && j > 0 && j < cols - 1) {
						if (this.sourceCell != null) {
							throw new IllegalArgumentException(
									"Map should only contain one source tile in any non-edge cell.");
						}
						this.sourceCell = terminationCell;
					} else {
						if (this.sinkCell != null) {
							throw new IllegalArgumentException(
									"Map should only contain one sink tile in any edge cell.");
						}
						this.sinkCell = terminationCell;
					}
				}
			}
		}
		if (this.sourceCell == null) {
			throw new IllegalArgumentException("Map must contain one source tile.");
		}
		if (this.sinkCell == null) {
			throw new IllegalArgumentException("Map must contain one sink tile.");
		}
		Coordinate sourceCoord = this.sourceCell.coord;
		Direction sourceDirection = this.sourceCell.getDirection();
		Coordinate nextCoord = sourceCoord.add(sourceDirection.getOffset());
		if (nextCoord.row < 0 || nextCoord.row >= rows || nextCoord.col < 0 || nextCoord.col >= cols
				|| cells[nextCoord.row][nextCoord.col] instanceof Wall) {
			throw new IllegalArgumentException("The source tile must not point into a wall.");
		}
		Coordinate sinkCoord = this.sinkCell.coord;
		Direction sinkDirection = this.sinkCell.getDirection();
		Coordinate nextSinkCoord = sinkCoord.add(sinkDirection.getOffset());
		if (nextSinkCoord.row >= 0 && nextSinkCoord.row < rows && nextSinkCoord.col >= 0 && nextSinkCoord.col < cols) {
			throw new IllegalArgumentException("The sink tile must point outside the map.");
		}
	}

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
		filledTiles.add(new Coordinate(row - 1, col - 1));
		return true;
	}

	/**
	 * Undoes a step from the map. <p> Effectively replaces the cell with an empty cell in the coordinate specified. </p>
	 * @param coord  Coordinate to reset.
	 * @throws IllegalArgumentException  if the cell is not an instance of  {@link FillableCell} .
	 */
	public void undo(final Coordinate coord) {
		Cell cell = cells[coord.row][coord.col];
		if (!(cell instanceof FillableCell)) {
			throw new IllegalArgumentException("Cell is not an instance of FillableCell");
		}
		FillableCell fillableCell = (FillableCell) cell;
		fillableCell.removePipe();
		filledTiles.remove(coord);
	}

	/**
	 * Fills all pipes that are within  {@code  distance}  units from the  {@code  sourceCell} . <p> Hint: There are two ways to approach this. You can either iteratively fill the tiles by distance (i.e. filling  distance=0, distance=1, etc), or you can save the tiles you have already filled, and fill all adjacent cells of  the already-filled tiles. Whichever method you choose is up to you, as long as the result is the same. </p>
	 * @param distance  Distance to fill pipes.
	 */
	public void fillTiles(int distance) {
		if (distance < 0) {
			return;
		}
		Set<Coordinate> newlyFilled = new HashSet<>();
		Set<Coordinate> toExplore = new HashSet<>();
		newlyFilled.add(sourceCell.coord);
		toExplore.add(sourceCell.coord);
		for (int d = 0; d < distance; d++) {
			Set<Coordinate> nextToExplore = new HashSet<>();
			for (Coordinate coord : toExplore) {
				for (Direction dir : Direction.values()) {
					Coordinate adjacent = coord.add(dir.getOffset());
					if (isValidCoordinate(adjacent) && !newlyFilled.contains(adjacent)
							&& cells[adjacent.row][adjacent.col] instanceof FillableCell) {
						FillableCell fillableCell = (FillableCell) cells[adjacent.row][adjacent.col];
						if (fillableCell.hasPipe()) {
							newlyFilled.add(adjacent);
							nextToExplore.add(adjacent);
						}
					}
				}
			}
			toExplore = nextToExplore;
		}
		for (Coordinate coord : newlyFilled) {
			((FillableCell) cells[coord.row][coord.col]).setFilled();
		}
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
			for (Direction dir : Direction.values()) {
				Coordinate next = current.add(dir.getOffset());
				if (isValidCoordinate(next) && !visited.contains(next)
						&& cells[next.row][next.col] instanceof FillableCell) {
					FillableCell fillableCell = (FillableCell) cells[next.row][next.col];
					if (fillableCell.hasPipe() && fillableCell.isFilled()) {
						queue.offer(next);
						visited.add(next);
					}
				}
			}
		}
		return false;
	}
}