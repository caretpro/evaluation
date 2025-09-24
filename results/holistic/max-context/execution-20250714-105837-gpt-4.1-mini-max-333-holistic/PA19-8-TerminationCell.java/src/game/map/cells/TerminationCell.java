
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;

/**
 * Represents a source or a sink {@link Cell}.
 */
public class TerminationCell extends Cell {

	private boolean isFilled = false;
	@NotNull
	public final Direction pointingTo;
	@NotNull
	public final Type type;

	/**
	 *
	 * @param coord coordination of this cell
	 * @param direction direction of this termination
	 * @param type type of this termination
	 */
	public TerminationCell(Coordinate coord, Direction direction, Type type) {
		super(coord);
		this.pointingTo = direction;
		this.type = type;
	}

	/**
	 * Sets this cell as filled.
	 */
	public void setFilled() {
		this.isFilled = true;
	}

	/**
	 * <p>
	 * Hint: use Unicode triangles to represent termination cells.
	 * </p>
	 *
	 * @return the character representation of a termination cell in game
	 */
	@Override
	public char toSingleChar() {
		// Unicode characters for filled and unfilled termination cells by direction:
		// UP: unfilled = △ (U+25B3), filled = ▲ (U+25B2)
		// DOWN: unfilled = ▽ (U+25BD), filled = ▼ (U+25BC)
		// LEFT: unfilled = ◁ (U+25C1), filled = ◀ (U+25C0)
		// RIGHT: unfilled = ▷ (U+25B7), filled = ▶ (U+25B6)
		return switch (pointingTo) {
			case UP -> isFilled ? '\u25B2' : '\u25B3';
			case DOWN -> isFilled ? '\u25BC' : '\u25BD';
			case LEFT -> isFilled ? '\u25C0' : '\u25C1';
			case RIGHT -> isFilled ? '\u25B6' : '\u25B7';
		};
	}

	public enum Type {
		SOURCE, SINK
	}

	/**
	 * Data class encapsulating the coordinate and direction of the {@link TerminationCell}.
	 */
	public static class CreateInfo {

		public final Coordinate coord;
		public final Direction dir;

		public CreateInfo(@NotNull Coordinate coord, @NotNull Direction dir) {
			this.coord = coord;
			this.dir = dir;
		}
	}
}