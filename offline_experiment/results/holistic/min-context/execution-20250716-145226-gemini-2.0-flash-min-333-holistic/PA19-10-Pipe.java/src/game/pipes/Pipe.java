
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
		filled = true;
	}

	/**
	 * @return Whether this pipe is filled.
	 */
	@Override
	public boolean getFilled() {
		return filled;
	}

	/**
	 * @return List of connections for this pipe.
	 * @throws IllegalStateException if {@code this} pipe cannot be identified.
	 */
	public Direction[] getConnections() {
		switch (shape) {
			case HORIZONTAL:
				return new Direction[]{Direction.WEST, Direction.EAST};
			case VERTICAL:
				return new Direction[]{Direction.NORTH, Direction.SOUTH};
			case TOP_LEFT:
				return new Direction[]{Direction.NORTH, Direction.WEST};
			case TOP_RIGHT:
				return new Direction[]{Direction.NORTH, Direction.EAST};
			case BOTTOM_LEFT:
				return new Direction[]{Direction.SOUTH, Direction.WEST};
			case BOTTOM_RIGHT:
				return new Direction[]{Direction.SOUTH, Direction.EAST};
			case CROSS:
				return new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
			default:
				throw new IllegalStateException("Unexpected value: " + shape);
		}
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
		switch (rep) {
			case "TL":
				return new Pipe(Shape.TOP_LEFT);
			case "TR":
				return new Pipe(Shape.TOP_RIGHT);
			case "BL":
				return new Pipe(Shape.BOTTOM_LEFT);
			case "BR":
				return new Pipe(Shape.BOTTOM_RIGHT);
			case "HZ":
				return new Pipe(Shape.HORIZONTAL);
			case "VT":
				return new Pipe(Shape.VERTICAL);
			case "CR":
				return new Pipe(Shape.CROSS);
			default:
				throw new IllegalArgumentException("Unknown pipe representation: " + rep);
		}
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