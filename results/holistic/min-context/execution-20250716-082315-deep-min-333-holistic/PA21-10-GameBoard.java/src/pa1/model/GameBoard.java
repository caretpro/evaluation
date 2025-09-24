
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GameBoard {

	private final int numRows;
	private final int numCols;

	@NotNull
	private final Cell[][] board;

	@NotNull
	private final Player player;

	public GameBoard(final int numRows, final int numCols, final Cell[][] cells) {
		if (numRows != cells.length || numCols != cells[0].length) {
			throw new IllegalArgumentException("Dimensions don't match");
		}

		this.numRows = numRows;
		this.numCols = numCols;
		this.board = cells;
		this.player = getSinglePlayer();

		if (getNumGems() == 0) {
			throw new IllegalArgumentException("No gems found");
		}

		if (!isAllGemsReachable()) {
			throw new IllegalArgumentException("Not all gems are reachable");
		}
	}

	@NotNull
	private Player getSinglePlayer() {
		Player player = null;
		for (final var row : board) {
			for (final var cell : row) {
				if (cell instanceof EntityCell ec && ec.getEntity() instanceof Player p) {
					if (player != null) {
						throw new IllegalArgumentException("Multiple players found");
					}
					player = p;
				}
			}
		}

		if (player == null) {
			throw new IllegalArgumentException("No player found");
		}
		return player;
	}

	private boolean isAllGemsReachable() {
		final var expectedNumOfGems = getNumGems();
		final var initialPosition = Objects.requireNonNull(getPlayer().getOwner()).getPosition();
		final var playerReachableCells = getAllReachablePositions(initialPosition);

		int actualNumOfGems = 0;
		for (final var pos : playerReachableCells) {
			final var cell = getCell(pos);
			if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
				++actualNumOfGems;
			}
		}

		return expectedNumOfGems == actualNumOfGems;
	}

	public Cell[] getRow(final int row) {
		if (row < 0 || row >= numRows) {
			throw new IndexOutOfBoundsException("Invalid row index");
		}
		return board[row].clone();
	}

	public Cell[] getCol(final int col) {
		if (col < 0 || col >= numCols) {
			throw new IndexOutOfBoundsException("Invalid column index");
		}
		Cell[] column = new Cell[numRows];
		for (int i = 0; i < numRows; i++) {
			column[i] = board[i][col];
		}
		return column;
	}

	public Cell getCell(final int row, final int col) {
		if (row < 0 || row >= numRows || col < 0 || col >= numCols) {
			throw new IndexOutOfBoundsException("Invalid position");
		}
		return board[row][col];
	}

	public Cell getCell(final Position position) {
		return getCell(position.row(), position.col());
	}

	public EntityCell getEntityCell(final int row, final int col) {
		Cell cell = getCell(row, col);
		if (!(cell instanceof EntityCell)) {
			throw new IllegalArgumentException("Cell is not an EntityCell");
		}
		return (EntityCell) cell;
	}

	public EntityCell getEntityCell(final Position position) {
		return getEntityCell(position.row(), position.col());
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public Player getPlayer() {
		return player;
	}

	public int getNumGems() {
		int count = 0;
		for (final var row : board) {
			for (final var cell : row) {
				if (cell instanceof EntityCell ec && ec.getEntity() instanceof Gem) {
					count++;
				}
			}
		}
		return count;
	}

	@Nullable
	private Position getEntityCellByOffset(@NotNull final Position pos, @NotNull final PositionOffset offset) {
		final var newPos = pos.offsetByOrNull(offset, getNumRows(), getNumCols());
		if (newPos == null) {
			return null;
		}
		if (getCell(newPos) instanceof Wall) {
			return null;
		}

		return newPos;
	}

	@NotNull
	private List<Position> getAllStoppablePositions(@NotNull final Position initialPosition) {
		Objects.requireNonNull(initialPosition);

		final List<Position> allStoppablePos = new ArrayList<>();
		final List<Position> posToTraverse = new ArrayList<>();

		posToTraverse.add(initialPosition);

		while (!posToTraverse.isEmpty()) {
			final var nextPos = posToTraverse.remove(posToTraverse.size() - 1);

			if (!(getCell(nextPos) instanceof EntityCell)) {
				continue;
			}
			if (allStoppablePos.contains(nextPos)) {
				continue;
			}
			allStoppablePos.add(nextPos);

			for (@NotNull final var dir : Direction.values()) {
				for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
					final var posOffset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
					final var posToAdd = getEntityCellByOffset(nextPos, posOffset);

					if (posToAdd == null) {
						final var maxDist = i - 1;
						if (maxDist > 0) {
							final var posOffsetBeforeThis = new PositionOffset(dir.getRowOffset() * maxDist,
									dir.getColOffset() * maxDist);
							final var posBeforeThis = getEntityCellByOffset(nextPos, posOffsetBeforeThis);

							if (posBeforeThis != null && !posToTraverse.contains(posBeforeThis)) {
								posToTraverse.add(posBeforeThis);
							}
						}

						break;
					}

					if (getCell(posToAdd) instanceof StopCell || isBorderCell(posToAdd, dir)) {
						posToTraverse.add(posToAdd);
					}
				}
			}
		}

		return Collections.unmodifiableList(allStoppablePos);
	}

	@NotNull
	private List<Position> getAllReachablePositions(@NotNull final Position initialPosition) {
		Objects.requireNonNull(initialPosition);

		final List<Position> allReachablePos = new ArrayList<>();
		final List<Position> allStoppablePos = getAllStoppablePositions(initialPosition);

		for (@NotNull final var reachablePos : allStoppablePos) {
			for (@NotNull final var dir : Direction.values()) {
				for (int i = 0; i < Math.max(getNumRows(), getNumCols()); ++i) {
					final var posOffset = new PositionOffset(dir.getRowOffset() * i, dir.getColOffset() * i);
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

	private boolean isBorderCell(@NotNull final Position cellPos, @NotNull final Direction dir) {
		return switch (dir) {
			case UP -> cellPos.row() == 0;
			case DOWN -> cellPos.row() == getNumRows() - 1;
			case LEFT -> cellPos.col() == 0;
			case RIGHT -> cellPos.col() == getNumCols() - 1;
		};
	}
}