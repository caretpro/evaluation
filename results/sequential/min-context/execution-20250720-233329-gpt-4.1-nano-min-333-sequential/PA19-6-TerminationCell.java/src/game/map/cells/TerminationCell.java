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

	public void TerminationCell(Coordinate coord, Direction direction, Type type) {
		super(coord);
		this.pointingTo = direction;
		this.type = type;
		this.isFilled = false;
	}

	public void setFilled() {
		this.isFilled = true;
	}

	@Override
	public char toSingleChar() {
		if (type == Type.SOURCE) {
			return PipePatterns.SOURCE;
		} else if (type == Type.SINK) {
			return PipePatterns.SINK;
		}
		return '?';
	}
}
