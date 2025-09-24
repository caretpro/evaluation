
package util;

import org.jetbrains.annotations.NotNull;

/**
 * Utility class for mapping pipe elements (including termination
 * cells) to their single‐character game representation.
 */
public final class PipePatterns {

    private PipePatterns() {
        // no instances
    }

    /**
     * Returns the character for a termination cell (source or sink).
     *
     * @param termType whether this is a SOURCE or SINK
     * @param pointingTo the direction the termination faces
     * @param filled whether the termination is currently filled
     * @return the corresponding single‐char symbol
     */
    public static char termination(@NotNull Type termType,
                                   @NotNull Direction pointingTo,
                                   boolean filled) {
        switch (termType) {
            case SOURCE:
                return filled
                    ? sourceFilledChar(pointingTo)
                    : sourceEmptyChar(pointingTo);
            case SINK:
                return filled
                    ? sinkFilledChar(pointingTo)
                    : sinkEmptyChar(pointingTo);
            default:
                throw new IllegalArgumentException("Unknown Type: " + termType);
        }
    }

    /**
     * Returns the character for a SOURCE termination cell.
     * (Convenience overload, defaults to SOURCE.)
     *
     * @param pointingTo the direction the source faces
     * @param filled whether the source is currently filled
     * @return the corresponding single‐char symbol
     */
    public static char termination(@NotNull Direction pointingTo,
                                   boolean filled) {
        return termination(Type.SOURCE, pointingTo, filled);
    }

    // -- internal helpers for each case --------------------------------------------------

    private static char sourceEmptyChar(@NotNull Direction d) {
        switch (d) {
            case NORTH: return '╹';
            case EAST:  return '╺';
            case SOUTH: return '╻';
            case WEST:  return '╸';
            default:    throw new AssertionError(d);
        }
    }

    private static char sourceFilledChar(@NotNull Direction d) {
        switch (d) {
            case NORTH: return '┻';
            case EAST:  return '┣';
            case SOUTH: return '┳';
            case WEST:  return '┫';
            default:    throw new AssertionError(d);
        }
    }

    private static char sinkEmptyChar(@NotNull Direction d) {
        switch (d) {
            case NORTH: return '╹';
            case EAST:  return '╺';
            case SOUTH: return '╻';
            case WEST:  return '╸';
            default:    throw new AssertionError(d);
        }
    }

    private static char sinkFilledChar(@NotNull Direction d) {
        switch (d) {
            case NORTH: return '┻';
            case EAST:  return '┣';
            case SOUTH: return '┳';
            case WEST:  return '┫';
            default:    throw new AssertionError(d);
        }
    }

    /**
     * SOURCE vs. SINK termination.
     */
    public enum Type {
        SOURCE, SINK
    }

    /**
     * Cardinal directions.
     */
    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }
}