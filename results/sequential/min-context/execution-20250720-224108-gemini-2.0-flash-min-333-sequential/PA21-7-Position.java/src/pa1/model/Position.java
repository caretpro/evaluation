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

	/**
	 * Creates a new instance of  {@link Position}  with the coordinates offset by the given amount.
	 * @param dRow  Number of rows to offset by.
	 * @param dCol  Number of columns to offset by.
	 * @return  A new instance of  {@link Position}  with the given offset applied.
	 * @throws IllegalArgumentException  if any component of the resulting coordinate is negative.
	 */
	public Position offsetBy(final int dRow, final int dCol) {
		int newRow = this.row + dRow;
		int newCol = this.col + dCol;
		if (newRow < 0 || newCol < 0) {
			throw new IllegalArgumentException("Position coordinates cannot be of a negative value.");
		}
		return new Position(newRow, newCol);
	}

	/**
	 * Creates a new instance of  {@link Position}  with the coordinates offset by the given amount.
	 * @param offset  The  {@link PositionOffset}  instance to offset this position by.
	 * @return  A new instance of  {@link Position}  with the given offset applied.
	 * @throws IllegalArgumentException  if any component of the resulting coordinate is negative.
	 */
	public Position offsetBy(final PositionOffset offset) {
		int newRow = this.row + offset.dRow();
		int newCol = this.col + offset.dCol();
		if (newRow < 0 || newCol < 0) {
			throw new IllegalArgumentException("Position coordinates cannot be of a negative value.");
		}
		return new Position(newRow, newCol);
	}

	/**
	 * Creates a new instance of  {@link Position}  with the coordinates offset by the given amount. If the resulting position is out-of-bounds (either because either coordinate is negative or exceeds  {@code  numRows}  or {@code  numCols} ), returns  {@code  null} .
	 * @param dRow     Number of rows to offset by.
	 * @param dCol     Number of columns to offset by.
	 * @param numRows  Number of rows of the game board.
	 * @param numCols  Number of columns of the game board.
	 * @return  A new instance of  {@link Position}  with the given offset applied.
	 */
	public Position offsetByOrNull(final int dRow, final int dCol, final int numRows, final int numCols) {
		int newRow = this.row + dRow;
		int newCol = this.col + dCol;
		if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
			return null;
		}
		return new Position(newRow, newCol);
	}

	/**
	 * Creates a new instance of  {@link Position}  with the coordinates offset by the given amount. If the resulting position is out-of-bounds (either because either coordinate is negative or exceeds  {@code  numRows}  or {@code  numCols} ), returns  {@code  null} .
	 * @param offset   The  {@link PositionOffset}  instance to offset this position by.
	 * @param numRows  Number of rows of the game board.
	 * @param numCols  Number of columns of the game board.
	 * @return  A new instance of  {@link Position}  with the given offset applied.
	 */
	public Position offsetByOrNull(final PositionOffset offset, final int numRows, final int numCols) {
		int newRow = this.row + offset.dRow();
		int newCol = this.col + offset.dCol();
		if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numCols) {
			return null;
		}
		return new Position(newRow, newCol);
	}
}
