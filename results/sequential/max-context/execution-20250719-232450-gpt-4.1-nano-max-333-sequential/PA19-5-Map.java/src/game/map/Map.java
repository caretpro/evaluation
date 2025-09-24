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

	public void Map(int rows, int cols) {
		if (rows < 3 || cols < 3) {
			throw new IllegalArgumentException("Map size must be at least 3x3 to place source and sink properly.");
		}
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				cells[r][c] = new FillableCell(new Coordinate(r, c));
			}
		}
		Random rng = new Random();
		int sourceRow, sourceCol;
		Direction sourceDirection;
		do {
			sourceRow = rng.nextInt(rows - 2) + 1;
			sourceCol = rng.nextInt(cols - 2) + 1;
			Direction[] inwardDirections = { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
			sourceDirection = inwardDirections[rng.nextInt(inwardDirections.length)];
			break;
		} while (true);
		sourceCell = new TerminationCell(new Coordinate(sourceRow, sourceCol), sourceDirection,
				TerminationCell.Type.SOURCE);
		cells[sourceRow][sourceCol] = sourceCell;
		int sinkRow, sinkCol;
		Direction sinkDirection;
		boolean onTopOrBottom = rng.nextBoolean();
		if (onTopOrBottom) {
			sinkRow = rng.nextBoolean() ? 0 : rows - 1;
			sinkCol = rng.nextInt(cols);
			sinkDirection = (sinkRow == 0) ? Direction.UP : Direction.DOWN;
		} else {
			sinkCol = rng.nextBoolean() ? 0 : cols - 1;
			sinkRow = rng.nextInt(rows);
			sinkDirection = (sinkCol == 0) ? Direction.LEFT : Direction.RIGHT;
		}
		sinkCell = new TerminationCell(new Coordinate(sinkRow, sinkCol), sinkDirection, TerminationCell.Type.SINK);
		cells[sinkRow][sinkCol] = sinkCell;
	}

	public void Map(int rows, int cols, Cell[][] cells) {
		if (rows < 3 || cols < 3) {
			throw new IllegalArgumentException("Map size must be at least 3x3.");
		}
		if (cells.length != rows || Arrays.stream(cells).anyMatch(row -> row.length != cols)) {
			throw new IllegalArgumentException("Cells array dimensions do not match specified rows and columns.");
		}
		this.rows = rows;
		this.cols = cols;
		this.cells = new Cell[rows][cols];
		boolean sourceFound = false;
		boolean sinkFound = false;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Cell cell = cells[r][c];
				if (cell == null) {
					throw new IllegalArgumentException("Cell at (" + r + "," + c + ") is null.");
				}
				this.cells[r][c] = cell;
				if (cell instanceof TerminationCell) {
					TerminationCell termCell = (TerminationCell) cell;
					if (termCell.type == TerminationCell.Type.SOURCE) {
						if (sourceFound) {
							throw new IllegalArgumentException("Multiple source cells found.");
						}
						if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
							throw new IllegalArgumentException("Source cell must not be on the edge.");
						}
						Coordinate targetCoord = new Coordinate(r, c).add(termCell.pointingTo.getOffset());
						if (isWall(targetCoord)) {
							throw new IllegalArgumentException("Source points into a wall at " + targetCoord);
						}
						sourceCell = termCell;
						sourceFound = true;
					} else if (termCell.type == TerminationCell.Type.SINK) {
						if (!(r == 0 || r == rows - 1 || c == 0 || c == cols - 1)) {
							throw new IllegalArgumentException("Sink cell must be on the edge.");
						}
						Coordinate targetCoord = new Coordinate(r, c).add(termCell.pointingTo.getOffset());
						if (isInsideMap(targetCoord)) {
							throw new IllegalArgumentException("Sink points inside the map at " + targetCoord);
						}
						sinkCell = termCell;
						sinkFound = true;
					}
				}
			}
		}
		if (!sourceFound) {
			throw new IllegalArgumentException("Source cell not found.");
		}
		if (!sinkFound) {
			throw new IllegalArgumentException("Sink cell not found.");
		}
		this.sourceCell = sourceCell;
		this.sinkCell = sinkCell;
	}

	boolean tryPlacePipe(int row, int col, Pipe p) {
		int r = row - 1;
		int c = col - 1;
		if (r < 0 || r >= rows || c < 0 || c >= cols) {
			return false;
		}
		Cell targetCell = cells[r][c];
		if (!(targetCell instanceof FillableCell)) {
			return false;
		}
		if (targetCell instanceof game.map.cells.PipeCell) {
			return false;
		}
		cells[r][c] = new game.map.cells.PipeCell(new Coordinate(r, c), p);
		return true;
	}

	public void undo(final Coordinate coord) {
		int r = coord.row;
		int c = coord.col;
		if (r < 0 || r >= rows || c < 0 || c >= cols) {
			throw new IllegalArgumentException("Coordinate out of bounds");
		}
		Cell currentCell = cells[r][c];
		if (!(currentCell instanceof FillableCell)) {
			throw new IllegalArgumentException("Cell is not fillable");
		}
		cells[r][c] = new game.map.cells.EmptyCell(coord);
	}

	public void fillTiles(int distance) {
		if (sourceCell == null || cells == null || distance < 0) {
			return;
		}
		filledTiles.clear();
		Queue<Coordinate> queue = new ArrayDeque<>();
		Set<Coordinate> visited = new HashSet<>();
		Coordinate start = sourceCell.coord;
		queue.offer(start);
		visited.add(start);
		filledTiles.add(start);
		sourceCell.setFilled();
		int currentDistance = 0;
		while (!queue.isEmpty() && currentDistance < distance) {
			int levelSize = queue.size();
			for (int i = 0; i < levelSize; i++) {
				Coordinate current = queue.poll();
				for (Direction dir : Direction.values()) {
					Coordinate neighbor = current.add(dir.getOffset());
					if (neighbor.row < 0 || neighbor.row >= rows || neighbor.col < 0 || neighbor.col >= cols) {
						continue;
					}
					if (visited.contains(neighbor)) {
						continue;
					}
					Cell neighborCell = cells[neighbor.row][neighbor.col];
					if (neighborCell instanceof game.map.cells.FillableCell) {
						if (neighborCell instanceof game.map.cells.TerminationCell) {
							((TerminationCell) neighborCell).setFilled();
						} else if (neighborCell instanceof game.map.cells.PipeCell) {
							((game.map.cells.PipeCell) neighborCell).setFilled();
						} else if (neighborCell instanceof game.map.cells.EmptyCell) {
							((game.map.cells.EmptyCell) neighborCell).setFilled();
						}
						filledTiles.add(neighbor);
						queue.offer(neighbor);
						visited.add(neighbor);
					}
				}
			}
			currentDistance++;
		}
	}

	public boolean checkPath() {
		if (sourceCell == null || sinkCell == null) {
			return false;
		}
		Set<Coordinate> visited = new HashSet<>();
		Queue<Coordinate> queue = new ArrayDeque<>();
		queue.offer(sourceCell.coord);
		visited.add(sourceCell.coord);
		while (!queue.isEmpty()) {
			Coordinate current = queue.poll();
			if (current.equals(sinkCell.coord)) {
				return true;
			}
			Cell currentCell = cells[current.row][current.col];
			if (currentCell instanceof TerminationCell) {
				TerminationCell term = (TerminationCell) currentCell;
				if (!term.isFilled) {
					continue;
				}
			} else if (currentCell instanceof game.map.cells.PipeCell) {
				if (!((game.map.cells.PipeCell) currentCell).getFilled()) {
					continue;
				}
			} else if (!(currentCell instanceof game.map.cells.FillableCell)) {
				continue;
			}
			if (currentCell instanceof game.map.cells.PipeCell) {
				Direction[] connections = ((game.map.cells.PipeCell) currentCell).getConnections();
				for (Direction dir : connections) {
					Coordinate neighborCoord = current.add(dir.getOffset());
					if (neighborCoord.row < 0 || neighborCoord.row >= rows || neighborCoord.col < 0
							|| neighborCoord.col >= cols) {
						continue;
					}
					if (visited.contains(neighborCoord)) {
						continue;
					}
					Cell neighborCell = cells[neighborCoord.row][neighborCoord.col];
					boolean connectedBack = false;
					if (neighborCell instanceof game.map.cells.PipeCell) {
						Direction[] neighborConnections = ((game.map.cells.PipeCell) neighborCell).getConnections();
						for (Direction neighborDir : neighborConnections) {
							if (neighborCoord.add(neighborDir.getOffset()).equals(current)) {
								connectedBack = true;
								break;
							}
						}
					} else if (neighborCell instanceof TerminationCell) {
						TerminationCell termNeighbor = (TerminationCell) neighborCell;
						if (termNeighbor.coord.equals(sinkCell.coord)
								&& termNeighbor.type == TerminationCell.Type.SINK) {
							connectedBack = true;
						}
					} else if (neighborCell instanceof game.map.cells.FillableCell) {
						if (neighborCell instanceof game.map.cells.TerminationCell) {
							continue;
						}
					}
					if (connectedBack || neighborCell instanceof TerminationCell) {
						visited.add(neighborCoord);
						queue.offer(neighborCoord);
					}
				}
			} else if (currentCell instanceof TerminationCell) {
			}
		}
		return false;
	}

	public boolean hasLost() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Cell cell = cells[r][c];
				if (cell instanceof game.map.cells.PipeCell) {
					Pipe pipe = ((game.map.cells.PipeCell) cell).getPipe();
					if (pipe.getFilled()) {
						return false;
					}
				}
			}
		}
		return true;
	}
}
