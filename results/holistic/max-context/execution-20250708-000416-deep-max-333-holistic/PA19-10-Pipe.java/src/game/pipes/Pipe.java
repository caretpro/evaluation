
package game.pipes;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Direction;
import util.PipePatterns;

public class Pipe implements MapElement {

    @NotNull
    private final Shape shape;
    private boolean filled = false;

    public Pipe(@NotNull Shape shape) {
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
        return switch (shape) {
            case HORIZONTAL -> new Direction[]{Direction.LEFT, Direction.RIGHT};
            case VERTICAL -> new Direction[]{Direction.UP, Direction.DOWN};
            case TOP_LEFT -> new Direction[]{Direction.UP, Direction.LEFT};
            case TOP_RIGHT -> new Direction[]{Direction.UP, Direction.RIGHT};
            case BOTTOM_LEFT -> new Direction[]{Direction.DOWN, Direction.LEFT};
            case BOTTOM_RIGHT -> new Direction[]{Direction.DOWN, Direction.RIGHT};
            case CROSS -> Direction.values();
        };
    }

    @Override
    public char toSingleChar() {
        return shape.getCharByState(filled);
    }

    public static Pipe fromString(String rep) {
        if (rep == null) {
            throw new IllegalArgumentException("Representation cannot be null");
        }
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