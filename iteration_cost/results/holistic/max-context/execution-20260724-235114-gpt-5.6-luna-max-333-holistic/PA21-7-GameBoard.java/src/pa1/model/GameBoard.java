
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The main game board of the game.
 *
 * <p>
 * The top-left hand corner of the game board is the "origin" of the board (0, 0).
 * </p>
 */
public final class GameBoard {

	/**
	 * Number of rows in the game board.
	 */
	private final int numRows;

	/**
	 * Number of columns in the game board.
	 */
	private final int numCols;

	/**
	 * 2D array representing each cell in the game board.
	 */
	@NotNull
	private final Cell[][] board;

	/**
	 * The instance of {@link Player} on this game board.
	 */
	@NotNull
	private final Player player;

	/**
	 * Creates an instance using the provided creation parameters.
	 *
	 * @param numRows The number of rows in the game board.
	 * @param numCols The number of columns in the game board.
	 * @param cells   The initial values of cells.
	 * @throws IllegalArgumentException if the board dimensions are invalid, there is not exactly one player,
	 *                                  there are no gems, or some gems cannot be reached.
	 */
	public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
		if (numRows <= 0 || numCols <= 0) {
			throw new IllegalArgumentException("Board dimensions must be positive.");
		}
		if (cells == null) {
			throw new IllegalArgumentException("Cells cannot be null.");
		}
		if (numRows != cells.length) {
			throw new IllegalArgumentException("The number of rows does not match the cells array.");
		}

		final Cell[][] copiedBoard = new Cell[numRows][numCols];

		for (int row = 0; row < numRows; row++) {
			if (cells[row] == null || cells[row].length != numCols) {
				throw new IllegalArgumentException("The number of columns does not match the cells array.");
			}

			for (int col = 0; col < numCols; col++) {
				if (cells[row][col] == null) {
					throw new IllegalArgumentException("Board cells cannot be null.");
				}
				copiedBoard[row][col] = cells[row][col];
			}
		}

		this.numRows = numRows;
		this.numCols = numCols;
		this.board = copiedBoard;
		this.player = getSinglePlayer();

		if (getNumGems() == 0) {
			throw new IllegalArgumentException("The board must contain at least one gem.");
		}
		if (!isAllGemsReachable()) {
			throw new IllegalArgumentException("Not all gems are reachable by the player.");
		}
	}

	/**
	 * Checks that a single player exists on the game board, and returns the instance of the player.
	 *
	 * @return The single instance of player on the game board.
	 * @throws IllegalArgumentException if the game board has zero, or more than one player entity.
	 */
	@NotNull
	private Player getSinglePlayer() {
		Player player = null;

		for (final var row : board) {
			for (final var cell : row) {
				if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
					if (player != null) {
						throw new IllegalArgumentException("The board contains more than one player.");
					}
					player = p;
				}
			}
		}

		if (player == null) {
			throw new IllegalArgumentException("The board does not contain a player.");
		}

		return player;
	}

	/**
	 * Retrieves the position of a non-wall cell using an offset.
	 *
	 * @param pos    Position.
	 * @param offset Positional offset to apply.
	 * @return The resulting position, or {@code null} if it is out of bounds or is a wall.
	 */
	@Nullable
	private Position getEntityCellByOffset(
			@NotNull final Position pos,
			@NotNull final PositionOffset offset) {
		final var newPos = pos.offsetByOrNull(offset, getNumRows(), getNumCols());

		if (newPos == null || getCell(newPos) instanceof Wall) {
			return null;
		}

		return newPos;
	}

	/**
	 * Gets all positions on which the player can stop from the initial position.
	 *
	 * @param initialPosition The starting position.
	 * @return All stoppable positions.
	 */
	@NotNull
	private List<Position> getAllStoppablePositions(@NotNull final Position initialPosition) {
		Objects.requireNonNull(initialPosition);

		final List<Position> allStoppablePos = new ArrayList<>();
		final List<Position> posToTraverse = new ArrayList<>();
		posToTraverse.add(initialPosition);

		while (!posToTraverse.isEmpty()) {
			final var nextPos = posToTraverse.remove(posToTraverse.size() - 1);

			if (!(getCell(nextPos) instanceof EntityCell)
					|| allStoppablePos.contains(nextPos)) {
				continue;
			}

			allStoppablePos.add(nextPos);

			for (final var dir : Direction.values()) {
				for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
					final var posOffset = new PositionOffset(
							dir.getRowOffset() * i,
							dir.getColOffset() * i);

					final var posToAdd = getEntityCellByOffset(nextPos, posOffset);

					if (posToAdd == null) {
						final int maxDist = i - 1;

						if (maxDist > 0) {
							final var previousOffset = new PositionOffset(
									dir.getRowOffset() * maxDist,
									dir.getColOffset() * maxDist);

							final var previousPos =
									getEntityCellByOffset(nextPos, previousOffset);

							if (previousPos != null && !posToTraverse.contains(previousPos)) {
								posToTraverse.add(previousPos);
							}
						}

						break;
					}

					if (getCell(posToAdd) instanceof StopCell
							|| isBorderCell(posToAdd, dir)) {
						if (!posToTraverse.contains(posToAdd)) {
							posToTraverse.add(posToAdd);
						}
					}
				}
			}
		}

		return Collections.unmodifiableList(allStoppablePos);
	}

	/**
	 * Gets all positions reachable from the initial position.
	 *
	 * @param initialPosition The starting position.
	 * @return All reachable positions.
	 */
	@NotNull
	private List<Position> getAllReachablePositions(@NotNull final Position initialPosition) {
		Objects.requireNonNull(initialPosition);

		final List<Position> allReachablePos = new ArrayList<>();

		for (final var reachablePos : getAllStoppablePositions(initialPosition)) {
			for (final var dir : Direction.values()) {
				for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
					final var posOffset = new PositionOffset(
							dir.getRowOffset() * i,
							dir.getColOffset() * i);

					final var posToAdd = getEntityCellByOffset(reachablePos, posOffset);

					if (posToAdd == null) {
						break;
					}

					if (!allReachablePos.contains(posToAdd)) {
						allReachablePos.add(posToAdd);
					}
				}
			}
		}

		return Collections.unmodifiableList(allReachablePos);
	}

	/**
	 * Checks whether the given cell is a border cell in the specified direction.
	 *
	 * @param cellPos Cell position.
	 * @param dir Direction.
	 * @return Whether the cell is a border cell.
	 */
	private boolean isBorderCell(
			@NotNull final Position cellPos,
			@NotNull final Direction dir) {
		return switch (dir) {
			case UP -> cellPos.row() == 0;
			case DOWN -> cellPos.row() == getNumRows() - 1;
			case LEFT -> cellPos.col() == 0;
			case RIGHT -> cellPos.col() == getNumCols() - 1;
		};
	}

	/**
	 * Checks whether all gems are reachable from the player's initial position.
	 *
	 * @return {@code true} if all gems are reachable.
	 */
	private boolean isAllGemsReachable() {
		final int expectedNumOfGems = getNumGems();
		final var owner = Objects.requireNonNull(getPlayer().getOwner());
		final var playerReachableCells = getAllReachablePositions(owner.getPosition());

		int actualNumOfGems = 0;

		for (final var pos : playerReachableCells) {
			final var cell = getCell(pos);

			if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
				++actualNumOfGems;
			}
		}

		return expectedNumOfGems == actualNumOfGems;
	}

	/**
	 * Returns the cells of a single row.
	 *
	 * @param row Row index.
	 * @return A copy of the requested row.
	 */
	@NotNull
	public Cell[] getRow(final int row) {
		if (row < 0 || row >= numRows) {
			throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
		}

		return board[row].clone();
	}

	/**
	 * Returns the cells of a single column.
	 *
	 * @param col Column index.
	 * @return The requested column.
	 */
	@NotNull
	public Cell[] getCol(final int col) {
		if (col < 0 || col >= numCols) {
			throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
		}

		final Cell[] result = new Cell[numRows];

		for (int row = 0; row < numRows; row++) {
			result[row] = board[row][col];
		}

		return result;
	}

	/**
	 * Returns a single cell of the game board.
	 *
	 * @param row Row index.
	 * @param col Column index.
	 * @return The cell at the specified location.
	 */
	@NotNull
	public Cell getCell(final int row, final int col) {
		if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
			throw new IndexOutOfBoundsException(
					"Cell position out of bounds: (" + row + ", " + col + ")");
		}

		return board[row][col];
	}

	/**
	 * Returns a single cell of the game board.
	 *
	 * @param position Cell position.
	 * @return The cell at the specified location.
	 */
	@NotNull
	public Cell getCell(@NotNull final Position position) {
		Objects.requireNonNull(position);
		return getCell(position.row(), position.col());
	}

	/**
	 * Returns an entity cell on the game board.
	 *
	 * @param row Row index.
	 * @param col Column index.
	 * @return The entity cell at the specified location.
	 * @throws IllegalArgumentException if the cell is not an entity cell.
	 */
	@NotNull
	public EntityCell getEntityCell(final int row, final int col) {
		return getEntityCell(new Position(row, col));
	}

	/**
	 * Returns an entity cell on the game board.
	 *
	 * @param position Cell position.
	 * @return The entity cell at the specified location.
	 * @throws IllegalArgumentException if the cell is not an entity cell.
	 */
	@NotNull
	public EntityCell getEntityCell(@NotNull final Position position) {
		final var cell = getCell(position);

		if (!(cell instanceof EntityCell entityCell)) {
			throw new IllegalArgumentException(
					"The cell at the specified position is not an EntityCell.");
		}

		return entityCell;
	}

	/**
	 * @return The number of rows in this game board.
	 */
	public int getNumRows() {
		return numRows;
	}

	/**
	 * @return The number of columns in this game board.
	 */
	public int getNumCols() {
		return numCols;
	}

	/**
	 * @return The player instance.
	 */
	@NotNull
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return The number of gems still present in the game board.
	 */
	public int getNumGems() {
		int numberOfGems = 0;

		for (final var row : board) {
			for (final var cell : row) {
				if (cell instanceof EntityCell entityCell
						&& entityCell.getEntity() instanceof Gem) {
					++numberOfGems;
				}
			}
		}

		return numberOfGems;
	}
}