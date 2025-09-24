
package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

import java.util.Arrays;
import java.util.Objects;

/**
 * A pipe element in the game board.
 */
public class Pipe implements MapElement {

    @NotNull
    private final Shape shape;
    private boolean filled = false;

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
     * @throws NullPointerException     if {@code rep} is null.
     */
    public static Pipe fromString(String rep) {
        Objects.requireNonNull(rep, "rep must not be null");
        return switch (rep) {
            case "HZ" -> new Pipe(Shape.HORIZONTAL);
            case "VT" -> new Pipe(Shape.VERTICAL);
            case "TL" -> new Pipe(Shape.TOP_LEFT);
            case "TR" -> new Pipe(Shape.TOP_RIGHT);
            case "BL" -> new Pipe(Shape.BOTTOM_LEFT);
            case "BR" -> new Pipe(Shape.BOTTOM_RIGHT);
            case "CR" -> new Pipe(Shape.CROSS);
            default   -> throw new IllegalArgumentException("Unknown pipe representation: " + rep);
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

    /**
     * Creates a new pipe with a given shape.
     *
     * @param shape Shape of the pipe.
     * @throws NullPointerException if {@code shape} is null.
     */
    public Pipe(Shape shape) {
        this.shape = Objects.requireNonNull(shape, "shape must not be null");
    }

    /**
     * Marks this pipe as filled.
     */
    public void setFilled() {
        this.filled = true;
    }

    /**
     * @return Whether this pipe is filled.
     */
    public boolean getFilled() {
        return filled;
    }

    /**
     * @return List of connections for this pipe.
     * @throws IllegalStateException if this pipe cannot be identified.
     */
    public Direction[] getConnections() {
        Integer pattern = PipePatterns.Deconstructed.CONNECTIONS.get(shape);
        if (pattern == null) {
            throw new IllegalStateException("Unknown pipe shape: " + shape);
        }
        return Arrays.stream(Direction.values())
                     .filter(dir -> (pattern & dir.getBitMask()) != 0)
                     .toArray(Direction[]::new);
    }

    /**
     * @return The character representation of this pipe. Note that the representation is different
     * for filled and unfilled pipes.
     */
    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }
}