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

	public void Pipe(Shape shape) {
		if (shape == null) {
			throw new IllegalArgumentException("Shape cannot be null");
		}
		this.shape = shape;
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

	public Direction[] getConnections() {
		return switch (shape) {
		case HORIZONTAL:
			yield new Direction[] { Direction.LEFT, Direction.RIGHT };
		case VERTICAL:
			yield new Direction[] { Direction.UP, Direction.DOWN };
		case TOP_LEFT:
			yield new Direction[] { Direction.UP, Direction.LEFT };
		case TOP_RIGHT:
			yield new Direction[] { Direction.UP, Direction.RIGHT };
		case BOTTOM_LEFT:
			yield new Direction[] { Direction.DOWN, Direction.LEFT };
		case BOTTOM_RIGHT:
			yield new Direction[] { Direction.DOWN, Direction.RIGHT };
		case CROSS:
			yield new Direction[] { Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT };
		default:
			throw new IllegalStateException("Unknown pipe shape: " + shape);
		};
	}

	@Override
	public char toSingleChar() {
		return shape.getCharByState(filled);
	}

	public static Pipe fromString(String rep) {
		if (rep == null) {
			throw new IllegalArgumentException("Input string cannot be null");
		}
		return switch (rep) {
		case "HZ":
			yield new Pipe(Shape.HORIZONTAL);
		case "VT":
			yield new Pipe(Shape.VERTICAL);
		case "TL":
			yield new Pipe(Shape.TOP_LEFT);
		case "TR":
			yield new Pipe(Shape.TOP_RIGHT);
		case "BL":
			yield new Pipe(Shape.BOTTOM_LEFT);
		case "BR":
			yield new Pipe(Shape.BOTTOM_RIGHT);
		case "CR":
			yield new Pipe(Shape.CROSS);
		default:
			throw new IllegalArgumentException("Unknown pipe representation: " + rep);
		};
	}
}
