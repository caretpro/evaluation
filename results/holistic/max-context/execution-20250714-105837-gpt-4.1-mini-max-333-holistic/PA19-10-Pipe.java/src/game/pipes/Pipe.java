
package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

public class Pipe implements MapElement {

	@NotNull
	private final Shape shape;
	private boolean filled = false;

	/**
	 * Creates a new pipe with a given shape.
	 *
	 * @param shape Shape of the pipe.
	 */
	public Pipe(Shape shape) {
		this.shape = shape;
	}

	/**
	 * Sets the pipe as filled.
	 */
	public void setFilled() {
		this.filled = true;
	}

	/**
	 * @return Whether this pipe is filled.
	 */
	public boolean getFilled() {
		return this.filled;
	}

	/**
	 * @return List of connections for this pipe.
	 * @throws IllegalStateException if {@code this} pipe cannot be identified.
	 */
	public Direction[] getConnections() {
		return switch (shape) {
			case HORIZONTAL -> new Direction[]{Direction.LEFT, Direction.RIGHT};
			case VERTICAL -> new Direction[]{Direction.UP, Direction.DOWN};
			case TOP_LEFT -> new Direction[]{Direction.UP, Direction.LEFT};
			case TOP_RIGHT -> new Direction[]{Direction.UP, Direction.RIGHT};
			case BOTTOM_LEFT -> new Direction[]{Direction.DOWN, Direction.LEFT};
			case BOTTOM_RIGHT -> new Direction[]{Direction.DOWN, Direction.RIGHT};
			case CROSS -> new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
		};
	}

	/**
	 * @return The character representation of this pipe. Note that the representation is different for filled and
	 * unfilled pipes.
	 */
	@Override
	public char toSingleChar() {
		return shape.getCharByState(filled);
	}

	/**
	 * Converts a String to a Pipe.
	 *
	 * <p>
	 * Here is the list of ASCII representation to the pipes:
	 * TL: Top-Left
	 * TR: Top-Right
	 * BL: Bottom-Left
	 * BR: Bottom-Right
	 * HZ: Horizontal
	 * VT: Vertical
	 * CR: Cross
	 * </p>
	 *
	 * @param rep String representation of the pipe. For example, "HZ" corresponds to a pipe of horizontal shape.
	 * @return Pipe identified by the string.
	 * @throws IllegalArgumentException if the String does not represent a known pipe.
	 */
	public static Pipe fromString(String rep) {
		return switch (rep) {
			case "HZ" -> new Pipe(Shape.HORIZONTAL);
			case "VT" -> new Pipe(Shape.VERTICAL);
			case "TL" -> new Pipe(Shape.TOP_LEFT);
			case "TR" -> new Pipe(Shape.TOP_RIGHT);
			case "BL" -> new Pipe(Shape.BOTTOM_LEFT);
			case "BR" -> new Pipe(Shape.BOTTOM_RIGHT);
			case "CR" -> new Pipe(Shape.CROSS);
			default -> throw new IllegalArgumentException("Unknown pipe representation: " + rep);
		};
	}

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
}