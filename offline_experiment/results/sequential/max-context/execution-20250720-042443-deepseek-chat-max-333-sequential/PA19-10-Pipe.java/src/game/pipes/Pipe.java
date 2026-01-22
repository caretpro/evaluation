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
	public void Pipe(@NotNull Shape shape) {
		this.shape = shape;
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
		return filled;
	}

	/**
	 * @return  List of connections for this pipe.
	 * @throws IllegalStateException  if  {@code  this}  pipe cannot be identified.
	 */
	public Direction[] getConnections() {
		switch (shape) {
		case HORIZONTAL:
			return new Direction[] { Direction.LEFT, Direction.RIGHT };
		case VERTICAL:
			return new Direction[] { Direction.UP, Direction.DOWN };
		case TOP_LEFT:
			return new Direction[] { Direction.UP, Direction.LEFT };
		case TOP_RIGHT:
			return new Direction[] { Direction.UP, Direction.RIGHT };
		case BOTTOM_LEFT:
			return new Direction[] { Direction.DOWN, Direction.LEFT };
		case BOTTOM_RIGHT:
			return new Direction[] { Direction.DOWN, Direction.RIGHT };
		case CROSS:
			return new Direction[] { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
		default:
			throw new IllegalStateException("Pipe cannot be identified");
		}
	}

	/**
	 * @return  The character representation of this pipe. Note that the representation is different for filled and unfilled pipes.
	 */
	@Override
	public char toSingleChar() {
		return shape.getCharByState(filled);
	}

	public static Pipe fromString(String rep) {
		if (rep == null) {
			throw new IllegalArgumentException("String representation cannot be null");
		}
		Shape shape;
		switch (rep.toUpperCase()) {
		case "HZ":
			shape = Shape.HORIZONTAL;
			break;
		case "VT":
			shape = Shape.VERTICAL;
			break;
		case "TL":
			shape = Shape.TOP_LEFT;
			break;
		case "TR":
			shape = Shape.TOP_RIGHT;
			break;
		case "BL":
			shape = Shape.BOTTOM_LEFT;
			break;
		case "BR":
			shape = Shape.BOTTOM_RIGHT;
			break;
		case "CR":
			shape = Shape.CROSS;
			break;
		default:
			throw new IllegalArgumentException("Unknown pipe representation: " + rep);
		}
		Pipe pipe = new Pipe();
		pipe.shape = shape;
		return pipe;
	}
}
