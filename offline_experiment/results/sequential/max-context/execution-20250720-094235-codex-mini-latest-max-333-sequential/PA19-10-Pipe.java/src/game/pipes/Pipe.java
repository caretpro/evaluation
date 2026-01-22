
package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;
import java.util.ArrayList;
import java.util.List;

/**
 * A pipe element on the map.
 */
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
     * @return List of connections for this pipe.
     * @throws IllegalStateException if {@code this} pipe cannot be identified.
     */
    public Direction[] getConnections() {
        // use the unfilled pattern to determine which sides connect
        char pattern = shape.unfilledChar;
        List<Direction> dirs = new ArrayList<>(4);
        if ((pattern & PipePatterns.Mask.UP)    != 0) dirs.add(Direction.UP);
        if ((pattern & PipePatterns.Mask.RIGHT) != 0) dirs.add(Direction.RIGHT);
        if ((pattern & PipePatterns.Mask.DOWN)  != 0) dirs.add(Direction.DOWN);
        if ((pattern & PipePatterns.Mask.LEFT)  != 0) dirs.add(Direction.LEFT);
        return dirs.toArray(new Direction[0]);
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
        if (rep == null) {
            throw new IllegalArgumentException("Representation must not be null");
        }
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

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public enum Shape {
        HORIZONTAL(PipePatterns.Filled.HORIZONTAL,      PipePatterns.Unfilled.HORIZONTAL),
        VERTICAL(PipePatterns.Filled.VERTICAL,          PipePatterns.Unfilled.VERTICAL),
        TOP_LEFT(PipePatterns.Filled.TOP_LEFT,          PipePatterns.Unfilled.TOP_LEFT),
        TOP_RIGHT(PipePatterns.Filled.TOP_RIGHT,        PipePatterns.Unfilled.TOP_RIGHT),
        BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT,    PipePatterns.Unfilled.BOTTOM_LEFT),
        BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT,  PipePatterns.Unfilled.BOTTOM_RIGHT),
        CROSS(PipePatterns.Filled.CROSS,                PipePatterns.Unfilled.CROSS);

        final char filledChar;
        final char unfilledChar;

        Shape(char filled, char unfilled) {
            this.filledChar   = filled;
            this.unfilledChar = unfilled;
        }

        char getCharByState(boolean isFilled) {
            return isFilled ? filledChar : unfilledChar;
        }
    }
}