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
	}

	public void setFilled() {
		this.filled = true;
	}

	public boolean getFilled() {
		return filled;
	}

	public Direction[] getConnections() {
		if (shape == null) {
			throw new IllegalStateException("Pipe shape is not initialized");
		}
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
			throw new IllegalStateException("Unknown pipe shape: " + shape);
		}
	}

	@Override
	public char toSingleChar() {
		if (shape == null) {
			throw new IllegalStateException("Pipe shape is not initialized");
		}
		return shape.getCharByState(filled);
	}

	public void Pipe(Shape shape) {
		if (shape == null) {
			throw new IllegalArgumentException("Shape cannot be null");
		}
		this.shape = shape;
	}
}
