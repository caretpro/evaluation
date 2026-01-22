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

	public void Pipe(@NotNull Shape shape) {
		this.shape = java.util.Objects.requireNonNull(shape, "shape must not be null");
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
		return switch (shape) {
		case HORIZONTAL:
			yield PipePatterns.Horizontal.CONNS;
		case VERTICAL:
			yield PipePatterns.Vertical.CONNS;
		case TOP_LEFT:
			yield PipePatterns.TopLeft.CONNS;
		case TOP_RIGHT:
			yield PipePatterns.TopRight.CONNS;
		case BOTTOM_LEFT:
			yield PipePatterns.BottomLeft.CONNS;
		case BOTTOM_RIGHT:
			yield PipePatterns.BottomRight.CONNS;
		case CROSS:
			yield PipePatterns.Cross.CONNS;
		default:
			throw new IllegalStateException("Unknown pipe shape: " + shape);
		};
	}

	/**
	 * @return  The character representation of this pipe. Note that the representation is different for filled and unfilled pipes.
	 */
	@Override
	public char toSingleChar() {
		return shape.getCharByState(filled);
	}

	public void Pipe(@NotNull Shape shape) {
		this.shape = requireNonNull(shape, "shape must not be null");
	}
}
