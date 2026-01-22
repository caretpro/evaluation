package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

public class Pipe implements MapElement {

	@NotNull
	private final Shape shape;
	private boolean filled = false;

	public enum Shape {
		HORIZONTAL(PipePatterns.Filled.HORIZONTAL, PipePatterns.Unfilled.HORIZONTAL),
		VERTICAL(PipePatterns.Filled.VERTICAL, PipePatterns.Unfilled.VERTICAL),
		TOP_LEFT(PipePatterns.Filled.TOP_LEFT, PipePatterns.Unfilled.TOP_LEFT),
		TOP_RIGHT(PipePatterns.Filled.TOP_RIGHT, PipePatterns.Unfilled.TOP_RIGHT),
		BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT, PipePatterns.Unfilled.BOTTOM_LEFT),
		BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT, PipePatterns.Unfilled.BOTTOM_RIGHT),
		CROSS(PipePatterns.Filled.CROSS, PipePatterns.Unfilled.CROSS);

		final char filledChar;
		final char unfilledChar;

		Shape(char filled, char unfilled) {
			this.filledChar = filled;
			this.unfilledChar = unfilled;
		}

		char getCharByState(boolean isFilled) {
			return isFilled ? filledChar : unfilledChar;
		}
	}

	/**
	 * Creates a new pipe with a given shape.
	 * @param shape  Shape of the pipe.
	 */
	public void Pipe(Shape shape) {
		this.shape = Objects.requireNonNull(shape, "shape must not be null");
		this.filled = false;
	}

	/**
	 * Sets the pipe as filled.
	 */
	public void setFilled() {
		this.filled = true;
	}

	/**
	 * @return  Whether this pipe is filled.
	 */
	public boolean getFilled() {
		return this.filled;
	}

	/**
	 * @return  List of connections for this pipe.
	 * @throws IllegalStateException  if  {@code  this}  pipe cannot be identified.
	 */
	public Direction[] getConnections() {
		int mask = switch (shape) {
		case HORIZONTAL:
			yield PipePatterns.Unfilled.HORIZONTAL;
		case VERTICAL:
			yield PipePatterns.Unfilled.VERTICAL;
		case TOP_LEFT:
			yield PipePatterns.Unfilled.TOP_LEFT;
		case TOP_RIGHT:
			yield PipePatterns.Unfilled.TOP_RIGHT;
		case BOTTOM_LEFT:
			yield PipePatterns.Unfilled.BOTTOM_LEFT;
		case BOTTOM_RIGHT:
			yield PipePatterns.Unfilled.BOTTOM_RIGHT;
		case CROSS:
			yield PipePatterns.Unfilled.CROSS;
		};
		List<Direction> dirs = new ArrayList<>();
		if ((mask & PipePatterns.NORTH) != 0)
			dirs.add(Direction.NORTH);
		if ((mask & PipePatterns.EAST) != 0)
			dirs.add(Direction.EAST);
		if ((mask & PipePatterns.SOUTH) != 0)
			dirs.add(Direction.SOUTH);
		if ((mask & PipePatterns.WEST) != 0)
			dirs.add(Direction.WEST);
		return dirs.toArray(new Direction[0]);
	}

	@Override
	public char toSingleChar() {
		return shape.getCharByState(filled);
	}

	public void Pipe(Shape shape) {
		this.shape = Objects.requireNonNull(shape, "shape must not be null");
		this.filled = false;
	}
}
