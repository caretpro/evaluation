
package game.pipes;

/**
 * Represents a Pipe with different shapes/types.
 */
public class Pipe {

    /**
     * Enum representing different pipe shapes/types.
     */
    public enum Shape {
        PIPE_TYPE_A,
        PIPE_TYPE_B,
        PIPE_TYPE_C
    }

    private final Shape shape;

    /**
     * Constructor accepting a shape.
     *
     * @param shape The shape of the pipe.
     */
    public Pipe(Shape shape) {
        this.shape = shape;
    }

    /**
     * Converts the pipe to a single character representation.
     *
     * @return A character representing the pipe.
     */
    public char toSingleChar() {
        switch (shape) {
            case PIPE_TYPE_A:
                return 'A';
            case PIPE_TYPE_B:
                return 'B';
            case PIPE_TYPE_C:
                return 'C';
            default:
                return '?';
        }
    }
}