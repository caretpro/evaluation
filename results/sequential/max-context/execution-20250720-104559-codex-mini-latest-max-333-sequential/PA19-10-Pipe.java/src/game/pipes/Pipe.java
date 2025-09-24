
package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A pipe element on the map.
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
        HORIZONTAL(PipePatterns.Filled.HORIZONTAL,  PipePatterns.Unfilled.HORIZONTAL),
        VERTICAL(  PipePatterns.Filled.VERTICAL,    PipePatterns.Unfilled.VERTICAL),
        TOP_LEFT(  PipePatterns.Filled.TOP_LEFT,    PipePatterns.Unfilled.TOP_LEFT),
        TOP_RIGHT( PipePatterns.Filled.TOP_RIGHT,   PipePatterns.Unfilled.TOP_RIGHT),
        BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT,PipePatterns.Unfilled.BOTTOM_LEFT),
        BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT,PipePatterns.Unfilled.BOTTOM_RIGHT),
        CROSS(     PipePatterns.Filled.CROSS,       PipePatterns.Unfilled.CROSS);

        final int filledPattern;
        final int unfilledPattern;

        Shape(int filledPattern, int unfilledPattern) {
            this.filledPattern = filledPattern;
            this.unfilledPattern = unfilledPattern;
        }
    }

    /**
     * Creates a new pipe with a given shape.
     * @param shape Shape of the pipe.
     */
    public Pipe(Shape shape) {
        this.shape = Objects.requireNonNull(shape, "shape must not be null");
    }

    /** Marks this pipe as filled (i.e. water has flowed through). */
    public void setFilled() {
        this.filled = true;
    }

    /** @return whether this pipe has been filled. */
    public boolean getFilled() {
        return filled;
    }

    /**
     * @return the directions in which this pipe is open.
     * @throws IllegalStateException if this pipe has an unknown pattern.
     */
    public Direction[] getConnections() {
        // Use PipePatterns.*.patternFor(...) exactly as the tests expect:
        int pat = filled
            ? PipePatterns.Filled.patternFor(shape)
            : PipePatterns.Unfilled.patternFor(shape);
        if (pat < 0) {
            throw new IllegalStateException("Unknown pipe pattern for shape " + shape + " (filled=" + filled + ")");
        }
        List<Direction> dirs = new ArrayList<>(4);
        for (Direction d : Direction.values()) {
            // Use the bitmask() method on Direction as originally defined:
            if ((pat & d.bitmask()) != 0) {
                dirs.add(d);
            }
        }
        return dirs.toArray(Direction[]::new);
    }

    /** @return the character representation of this pipe. */
    @Override
    public char toSingleChar() {
        // Delegate to PipePatterns.*.charFor(...) exactly as the tests expect:
        return filled
            ? PipePatterns.Filled.charFor(shape)
            : PipePatterns.Unfilled.charFor(shape);
    }
}