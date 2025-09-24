
package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A pipe that can be connected on up to four sides, can be filled from one of its open ends,
 * and renders a different character depending on whether it is filled.
 */
public class Pipe implements MapElement {

    @NotNull
    private final Shape shape;
    private boolean filled = false;
    private Direction filledFrom = null;

    /**
     * Creates a new pipe with a given shape.
     *
     * @param shape Shape of the pipe.
     */
    public Pipe(Shape shape) {
        this.shape = Objects.requireNonNull(shape, "shape must not be null");
    }

    /**
     * Attempts to fill this pipe from a given incoming direction.
     *
     * @param from the direction from which water would enter this pipe (or null for the very first pipe)
     * @return true if the pipe was empty and accepts filling from that direction; false otherwise
     */
    public boolean setFilledFrom(Direction from) {
        // allow null only on the very first pipe
        if (from == null) {
            if (!filled) {
                filled = true;
                filledFrom = null;
                return true;
            }
            return false;
        }
        // otherwise must match one of this pipe's open connections
        if (!filled && Arrays.asList(getConnections()).contains(from)) {
            filled = true;
            filledFrom = from;
            return true;
        }
        return false;
    }

    /**
     * @return Whether this pipe is filled.
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     * @return The direction from which this pipe was filled, or null if not yet filled (or if first pipe).
     */
    public Direction getFilledFrom() {
        return filledFrom;
    }

    /**
     * @return List of open directions for this pipe, in the order expected by the PipeTest.
     */
    public Direction[] getConnections() {
        return switch (shape) {
            case HORIZONTAL   -> new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL     -> new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT     -> new Direction[]{Direction.UP, Direction.RIGHT};
            case TOP_RIGHT    -> new Direction[]{Direction.UP, Direction.LEFT};
            case BOTTOM_LEFT  -> new Direction[]{Direction.DOWN, Direction.RIGHT};
            case BOTTOM_RIGHT -> new Direction[]{Direction.DOWN, Direction.LEFT};
            case CROSS        -> new Direction[]{Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
            default -> throw new IllegalStateException("Unknown pipe shape: " + shape);
        };
    }

    /**
     * @return The character representation of this pipe (filled vs. unfilled).
     */
    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    /**
     * Converts a two‑letter code into a Pipe instance.
     *
     * TL = Top‑Left, TR = Top‑Right, BL = Bottom‑Left, BR = Bottom‑Right,
     * HZ = Horizontal, VT = Vertical, CR = Cross.
     *
     * @throws IllegalArgumentException if rep is null or not one of the known codes.
     */
    public static Pipe fromString(String rep) {
        Objects.requireNonNull(rep, "rep must not be null");
        return new Pipe(Arrays.stream(Shape.values())
                .collect(Collectors.toMap(Shape::getCode, Function.identity()))
                .getOrDefault(rep, null)
        );
    }

    /**
     * All available pipe shapes, each with its filled/unfilled ASCII char and its two‑letter code.
     */
    public enum Shape {
        HORIZONTAL(PipePatterns.Filled.HORIZONTAL, PipePatterns.Unfilled.HORIZONTAL, "HZ"),
        VERTICAL   (PipePatterns.Filled.VERTICAL,   PipePatterns.Unfilled.VERTICAL,   "VT"),
        TOP_LEFT   (PipePatterns.Filled.TOP_LEFT,   PipePatterns.Unfilled.TOP_LEFT,   "TL"),
        TOP_RIGHT  (PipePatterns.Filled.TOP_RIGHT,  PipePatterns.Unfilled.TOP_RIGHT,  "TR"),
        BOTTOM_LEFT(PipePatterns.Filled.BOTTOM_LEFT,PipePatterns.Unfilled.BOTTOM_LEFT,"BL"),
        BOTTOM_RIGHT(PipePatterns.Filled.BOTTOM_RIGHT,PipePatterns.Unfilled.BOTTOM_RIGHT,"BR"),
        CROSS      (PipePatterns.Filled.CROSS,      PipePatterns.Unfilled.CROSS,      "CR");

        private final char filledChar;
        private final char unfilledChar;
        private final String code;

        Shape(char filledChar, char unfilledChar, String code) {
            this.filledChar = filledChar;
            this.unfilledChar = unfilledChar;
            this.code = code;
        }

        char getCharByState(boolean isFilled) {
            return isFilled ? filledChar : unfilledChar;
        }

        String getCode() {
            return code;
        }
    }
}