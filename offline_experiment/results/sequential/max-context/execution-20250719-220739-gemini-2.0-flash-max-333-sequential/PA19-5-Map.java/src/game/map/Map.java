
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
		return filledTiles.size() == prevFilledTiles && prevFilledDistance != null;
	}

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
				cells[i][j] = new FillableCell(new Coordinate(i, j));
			}
		}
		TerminationCell.CreateInfo sourceInfo = generateStartCellInfo();
		TerminationCell.CreateInfo sinkInfo = generateEndCellInfo();
		Coordinate sourceCoord = sourceInfo.coord;
		Direction sourceDir = sourceInfo.dir;
		sourceCell = new TerminationCell(sourceCoord, sourceDir, TerminationCell.Type.SOURCE);
		cells[sourceCoord.row][sourceCoord.col] = sourceCell;
		Coordinate sinkCoord = sinkInfo.coord;
		Direction sinkDir = sinkInfo.dir;
		sinkCell = new TerminationCell(sinkCoord, sinkDir, TerminationCell.Type.SINK);
		cells[sinkCoord.row][sinkCoord.col] = sinkCell;
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
		TerminationCell source = null;
		TerminationCell sink = null;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Cell cell = cells[i][j];
				if (cell instanceof TerminationCell) {
					TerminationCell terminationCell = (TerminationCell) cell;
					if (terminationCell.type == TerminationCell.Type.SOURCE) {
						if (source != null) {
							throw new IllegalArgumentException("Map contains more than one source tile.");
						}
						if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
							throw new IllegalArgumentException("Source tile must not be on the edge.");
						}
						source = terminationCell;
					} else {
						if (sink != null) {
							throw new IllegalArgumentException("Map contains more than one sink tile.");
						}
						if (i != 0 && i != rows - 1 && j != 0 && j != cols - 1) {
							throw new IllegalArgumentException("Sink tile must be on the edge.");
						}
						sink = terminationCell;
					}
				}
			}
		}
		if (source == null) {
			throw new IllegalArgumentException("Map does not contain a source tile.");
		}
		if (sink == null) {
			throw new IllegalArgumentException("Map does not contain a sink tile.");
		}
		this.sourceCell = source;
		this.sinkCell = sink;
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
			return;
		}
		Cell cell = cells[coord.row][coord.col];
		if (!(cell instanceof FillableCell)) {
			throw new IllegalArgumentException("Cell at " + coord + " is not a FillableCell.");
		}
		cells[coord.row][coord.col] = new FillableCell(coord);
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
		prevFilledTiles = filledTiles.size();
		prevFilledDistance = distance;
		Queue<Coordinate> queue = new LinkedList<>();
		Set<Coordinate> visited = new HashSet<>(filledTiles);
		queue.offer(sourceCell.coord);
		visited.add(sourceCell.coord);
		sourceCell.setFilled();
		int currentDistance = 0;
		while (!queue.isEmpty() && currentDistance <= distance) {
			int size = queue.size();
			for (int i = 0; i < size; i++) {
				Coordinate currentCoord = queue.poll();
				for (Direction dir : Direction.values()) {
					Coordinate neighborCoord = currentCoord.add(dir.getOffset());
					if (neighborCoord.row >= 0 && neighborCoord.row < rows && neighborCoord.col >= 0
							&& neighborCoord.col < cols) {
						Cell neighborCell = cells[neighborCoord.row][neighborCoord.col];
						if (neighborCell instanceof FillableCell) {
							FillableCell fillableCell = (FillableCell) neighborCell;
							Optional<Pipe> pipeOptional = fillableCell.getPipe();
							if (pipeOptional.isPresent()) {
								Pipe pipe = pipeOptional.get();
								boolean canFill = false;
								for (Direction connection : pipe.getConnections()) {
									if (currentCoord.add(connection.getOffset()).equals(neighborCoord)
											|| neighborCoord.add(connection.getOffset()).equals(currentCoord)) {
										canFill = true;
										break;
									}
								}
								if (canFill && !visited.contains(neighborCoord)) {
									pipe.setFilled();
									filledTiles.add(neighborCoord);
									visited.add(neighborCoord);
									queue.offer(neighborCoord);
								}
							}
						}
					}
				}
			}
			currentDistance++;
		}
	}

	public boolean checkPath() {
		Queue<Coordinate> queue = new LinkedList<>();
		Set<Coordinate> visited = new HashSet<>();
		queue.offer(sourceCell.coord);
		visited.add(sourceCell.coord);
		while (!queue.isEmpty()) {
			Coordinate currentCoord = queue.poll();
			if (currentCoord.equals(sinkCell.coord)) {
				return true;
			}
			Cell currentCell = cells[currentCoord.row][currentCoord.col];
			if (currentCell instanceof TerminationCell) {
				TerminationCell terminationCell = (TerminationCell) currentCell;
				Direction pointingTo = terminationCell.pointingTo;
				Coordinate nextCoord = currentCoord.add(pointingTo.getOffset());
				if (isValidCoordinate(nextCoord) && !visited.contains(nextCoord)) {
					queue.offer(nextCoord);
					visited.add(nextCoord);
				}
			} else if (currentCell instanceof FillableCell) {
				FillableCell fillableCell = (FillableCell) currentCell;
				Optional<Pipe> pipeOptional = fillableCell.getPipe();
				if (pipeOptional.isPresent()) {
					Pipe pipe = pipeOptional.get();
					for (Direction connection : pipe.getConnections()) {
						Coordinate nextCoord = currentCoord.add(connection.getOffset());
						if (isValidCoordinate(nextCoord) && !visited.contains(nextCoord)) {
							queue.offer(nextCoord);
							visited.add(nextCoord);
						}
					}
				}
			}
		}
		return false;
	}

	private boolean isValidCoordinate(Coordinate coord) {
		return coord.row >= 0 && coord.row < rows && coord.col >= 0 && coord.col < cols;
	}
}