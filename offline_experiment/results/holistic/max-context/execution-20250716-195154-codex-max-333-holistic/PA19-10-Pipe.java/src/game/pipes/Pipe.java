
package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A pipe on the map that can be filled or unfilled and has a particular shape.
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
     * @throws IllegalStateException if {@code this} pipe cannot be identified.
     */
    public Direction[] getConnections() {
        char pattern = shape.getCharByState(filled);
        return PipePatterns.getConnections(pattern);
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
     * Converts a two‑letter code to a Pipe.
     *
     * <p>
     * Here is the list of ASCII representation to the pipes:
     * TL: Top‑Left
     * TR: Top‑Right
     * BL: Bottom‑Left
     * BR: Bottom‑Right
     * HZ: Horizontal
     * VT: Vertical
     * CR: Cross
     * </p>
     *
     * @param rep two‑letter code of the pipe shape.
     * @return Pipe identified by the string.
     * @throws IllegalArgumentException if the String does not represent a known pipe.
     */
    public static Pipe fromString(String rep) {
        Map<String, Shape> lookup = Arrays.stream(Shape.values())
            .collect(Collectors.toMap(Shape::getCode, Function.identity()));
        Shape shape = lookup.get(rep);
        if (shape == null) {
            throw new IllegalArgumentException("Unknown pipe code: " + rep);
        }
        return new Pipe(shape);
    }

    public enum Shape {
        HORIZONTAL(PipePatterns.Filled.HORIZONTAL,   PipePatterns.Unfilled.HORIZONTAL,   "HZ"),
        VERTICAL  (PipePatterns.Filled.VERTICAL,     PipePatterns.Unfilled.VERTICAL,     "VT"),
        TOP_LEFT  (PipePatterns.Filled.TOP_LEFT,     PipePatterns.Unfilled.TOP_LEFT,     "TL"),
        TOP_RIGHT (PipePatterns.Filled.TOP_RIGHT,    PipePatterns.Unfilled.TOP_RIGHT,    "TR"),
        BOTTOM_LEFT (PipePatterns.Filled.BOTTOM_LEFT, PipePatterns.Unfilled.BOTTOM_LEFT, "BL"),
        BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT,PipePatterns.Unfilled.BOTTOM_RIGHT,"BR"),
        CROSS     (PipePatterns.Filled.CROSS,        PipePatterns.Unfilled.CROSS,        "CR");

        final char filledChar;
        final char unfilledChar;
        private final String code;

        Shape(char filled, char unfilled, String code) {
            this.filledChar = filled;
            this.unfilledChar = unfilled;
            this.code = code;
        }

        char getCharByState(boolean isFilled) {
            return isFilled ? filledChar : unfilledChar;
        }

        String getCode() {
            return this.code;
        }
    }
}