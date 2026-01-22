
package util;

import game.pipes.Pipe;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Provides the mapping from Pipe.Shape to its connection directions.
 */
public final class PipePatterns {

    private static final Map<Pipe.Shape, Direction[]> PATTERNS =
            new EnumMap<>(Pipe.Shape.class);

    static {
        PATTERNS.put(Pipe.Shape.HORIZONTAL,   new Direction[]{ Direction.LEFT, Direction.RIGHT });
        PATTERNS.put(Pipe.Shape.VERTICAL,     new Direction[]{ Direction.UP,   Direction.DOWN  });
        PATTERNS.put(Pipe.Shape.TOP_LEFT,     new Direction[]{ Direction.UP,   Direction.LEFT  });
        PATTERNS.put(Pipe.Shape.TOP_RIGHT,    new Direction[]{ Direction.UP,   Direction.RIGHT });
        PATTERNS.put(Pipe.Shape.BOTTOM_LEFT,  new Direction[]{ Direction.DOWN, Direction.LEFT  });
        PATTERNS.put(Pipe.Shape.BOTTOM_RIGHT, new Direction[]{ Direction.DOWN, Direction.RIGHT });
        PATTERNS.put(Pipe.Shape.CROSS,        new Direction[]{ Direction.UP,   Direction.RIGHT,
                                                              Direction.DOWN, Direction.LEFT });
    }

    private PipePatterns() { /* no instances */ }

    /**
     * Returns the array of {@link Direction}s corresponding to the given pipe shape.
     *
     * @param shape the pipe shape
     * @return an Optional containing the connection directions, or empty if unknown
     */
    public static Optional<Direction[]> connectionsForShape(@NotNull Pipe.Shape shape) {
        return Optional.ofNullable(PATTERNS.get(shape));
    }
}