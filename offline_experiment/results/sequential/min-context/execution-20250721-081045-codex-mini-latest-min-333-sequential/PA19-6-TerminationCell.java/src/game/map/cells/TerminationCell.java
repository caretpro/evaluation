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

	/**
	 * @param coord  coordination of this cell
	 * @param direction  direction of this termination
	 * @param type  type of this termination
	 */
	public void TerminationCell(Coordinate coord, Direction direction, Type type) {
		super(coord);
		this.pointingTo = direction;
		this.type = type;
		PipePatterns.register(coord, direction,
				type == Type.SOURCE ? PipePatterns.Termination.SOURCE : PipePatterns.Termination.SINK);
	}

	/**
	 * Sets this cell as filled.
	 */
	public void setFilled() {
		if (!isFilled) {
			isFilled = true;
			PipePatterns.register(getCoord(), pointingTo, type == Type.SOURCE ? PipePatterns.Termination.SOURCE_FILLED
					: PipePatterns.Termination.SINK_FILLED);
		}
	}

	protected Cell(Coordinate coord) {
		this.coord = coord;
	}
}
