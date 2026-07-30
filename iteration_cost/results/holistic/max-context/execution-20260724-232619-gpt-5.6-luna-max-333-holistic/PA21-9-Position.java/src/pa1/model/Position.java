
package pa1.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A position on the game board.
 */
public class Position {

	private int row;
	private int col;

	/**
	 * @param row The row number on the game board.
	 * @param col The column number on the game board.
	 * @throws IllegalArgumentException if any component of the coordinate is negative.
	 */
	public Position(int row, int col) {
		if (row < 0 || col < 0) {
			throw new IllegalArgumentException("Position coordinates cannot be of a negative value.");
		}
		this.row = row;
		this.col = col;
	}

	/**
	 * Creates a new position with the coordinates offset by the given amount.
	 *
	 * @param dRow Number of rows to offset by.
	 * @param dCol Number of columns to offset by.
	 * @return A new offset position.
	 * @throws IllegalArgumentException if the resulting coordinates are negative or cannot be represented as integers.
	 */
	public Position offsetBy(final int dRow, final int dCol) {
		final long newRow = (long) row + dRow;
		final long newCol = (long) col + dCol;

		if (newRow < 0 || newCol < 0
				|| newRow > Integer.MAX_VALUE
				|| newCol > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Position coordinates cannot be negative or exceed the integer range.");
		}

		return new Position((int) newRow, (int) newCol);
	}

	/**
	 * Creates a new position with the coordinates offset by the given amount.
	 *
	 * @param offset The offset to apply.
	 * @return A new offset position.
	 */
	public Position offsetBy(@NotNull final PositionOffset offset) {
		Objects.requireNonNull(offset, "offset cannot be null");
		return offsetBy(offset.dRow(), offset.dCol());
	}

	/**
	 * Creates a new offset position if it remains within the board.
	 *
	 * @param dRow Number of rows to offset by.
	 * @param dCol Number of columns to offset by.
	 * @param numRows Number of rows on the board.
	 * @param numCols Number of columns on the board.
	 * @return The offset position, or {@code null} if it is out-of-bounds.
	 */
	@Nullable
	public Position offsetByOrNull(
			final int dRow,
			final int dCol,
			final int numRows,
			final int numCols
	) {
		final long newRow = (long) row + dRow;
		final long newCol = (long) col + dCol;

		if (newRow < 0 || newCol < 0
				|| newRow >= numRows
				|| newCol >= numCols
				|| newRow > Integer.MAX_VALUE
				|| newCol > Integer.MAX_VALUE) {
			return null;
		}

		return new Position((int) newRow, (int) newCol);
	}

	/**
	 * Creates a new offset position if it remains within the board.
	 *
	 * @param offset The offset to apply.
	 * @param numRows Number of rows on the board.
	 * @param numCols Number of columns on the board.
	 * @return The offset position, or {@code null} if it is out-of-bounds.
	 */
	@Nullable
	public Position offsetByOrNull(
			@NotNull final PositionOffset offset,
			final int numRows,
			final int numCols
	) {
		Objects.requireNonNull(offset, "offset cannot be null");
		return offsetByOrNull(offset.dRow(), offset.dCol(), numRows, numCols);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Position position = (Position) o;
		return row == position.row && col == position.col;
	}

	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}

	public int row() {
		return row;
	}

	public void row(int row) {
		this.row = row;
	}

	public int col() {
		return col;
	}

	public void col(int col) {
		this.col = col;
	}
}