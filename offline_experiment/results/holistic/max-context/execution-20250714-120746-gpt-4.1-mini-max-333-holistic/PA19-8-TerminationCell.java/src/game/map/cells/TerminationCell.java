
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;
import util.PipePatterns;

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
	 * Returns the character representation of a termination cell in game.
	 * Uses {@link util.PipePatterns} constants for filled and unfilled source and sink cells.
	 * </p>
	 *
	 * @return the character representation of a termination cell in game
	 */
	@Override
	public char toSingleChar() {
		if (type == Type.SOURCE) {
			return switch (pointingTo) {
				case UP -> isFilled ? PipePatterns.SOURCE_UP_FILLED : PipePatterns.SOURCE_UP;
				case DOWN -> isFilled ? PipePatterns.SOURCE_DOWN_FILLED : PipePatterns.SOURCE_DOWN;
				case LEFT -> isFilled ? PipePatterns.SOURCE_LEFT_FILLED : PipePatterns.SOURCE_LEFT;
				case RIGHT -> isFilled ? PipePatterns.SOURCE_RIGHT_FILLED : PipePatterns.SOURCE_RIGHT;
			};
		} else { // SINK
			return switch (pointingTo) {
				case UP -> isFilled ? PipePatterns.SINK_UP_FILLED : PipePatterns.SINK_UP;
				case DOWN -> isFilled ? PipePatterns.SINK_DOWN_FILLED : PipePatterns.SINK_DOWN;
				case LEFT -> isFilled ? PipePatterns.SINK_LEFT_FILLED : PipePatterns.SINK_LEFT;
				case RIGHT -> isFilled ? PipePatterns.SINK_RIGHT_FILLED : PipePatterns.SINK_RIGHT;
			};
		}
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