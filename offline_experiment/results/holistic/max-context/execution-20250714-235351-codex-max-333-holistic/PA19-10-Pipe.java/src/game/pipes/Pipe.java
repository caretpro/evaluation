
package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

import java.util.Arrays;

/**
 * A pipe segment with a fixed shape that can be filled or unfilled,
 * has a single‐character representation, and knows which Directions it connects to.
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
     * @throws IllegalStateException if this pipe cannot be identified.
     */
    public Direction[] getConnections() {
        Direction[] conns = filled
            ? PipePatterns.FILLED.get(shape)
            : PipePatterns.UNFILLED.get(shape);
        if (conns == null) {
            throw new IllegalStateException("No pattern for pipe state=" + filled + ", shape=" + shape);
        }
        return conns;
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
        return Arrays.stream(Shape.values())
                     .filter(s -> s.name().equalsIgnoreCase(rep))
                     .findFirst()
                     .map(Pipe::new)
                     .orElseThrow(() ->
                         new IllegalArgumentException("Unknown pipe representation: " + rep)
                     );
    }

    public enum Shape {
        HORIZONTAL(PipePatterns.FILLED_CHAR.get("HZ"), PipePatterns.UNFILLED_CHAR.get("HZ")),
        VERTICAL(PipePatterns.FILLED_CHAR.get("VT"), PipePatterns.UNFILLED_CHAR.get("VT")),
        TOP_LEFT(PipePatterns.FILLED_CHAR.get("TL"), PipePatterns.UNFILLED_CHAR.get("TL")),
        TOP_RIGHT(PipePatterns.FILLED_CHAR.get("TR"), PipePatterns.UNFILLED_CHAR.get("TR")),
        BOTTOM_LEFT(PipePatterns.FILLED_CHAR.get("BL"), PipePatterns.UNFILLED_CHAR.get("BL")),
        BOTTOM_RIGHT(PipePatterns.FILLED_CHAR.get("BR"), PipePatterns.UNFILLED_CHAR.get("BR")),
        CROSS(PipePatterns.FILLED_CHAR.get("CR"), PipePatterns.UNFILLED_CHAR.get("CR"));

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