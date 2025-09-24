
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

	public Position offsetBy(final int dRow, final int dCol) {
		int newRow = row + dRow;
		int newCol = col + dCol;
		if (newRow < 0 || newCol < 0) {
			throw new IllegalArgumentException("Resulting position cannot be negative.");
		}
		return new Position(newRow, newCol);
	}

	public Position offsetBy(final PositionOffset offset) {
		return offsetBy(offset.dRow(), offset.dCol());
	}

	public Position offsetByOrNull(final int dRow, final int dCol, final int numRows, final int numCols) {
		int newRow = row + dRow;
		int newCol = col + dCol;
		if (newRow < 0 || newCol < 0 || newRow >= numRows || newCol >= numCols) {
			return null;
		}
		return new Position(newRow, newCol);
	}

	public Position offsetByOrNull(final PositionOffset offset, final int numRows, final int numCols) {
		return offsetByOrNull(offset.dRow(), offset.dCol(), numRows, numCols);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
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