
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;
import util.PipePatterns;

/**
 * Represents a source or a sink {@link Cell}.
 */
public class TerminationCell extends Cell {

    private boolean isFilled = false;
    @NotNull
    public final Direction pointingTo;
    @NotNull
    public final Type type;

    /**
     *
     * @param coord coordination of this cell
     * @param direction direction of this termination
     * @param type type of this termination
     */
    public TerminationCell(@NotNull Coordinate coord, @NotNull Direction direction, @NotNull Type type) {
        super(coord);
        this.pointingTo = direction;
        this.type = type;
    }

    /**
     * Sets this cell as filled.
     */
    public void setFilled() {
        this.isFilled = true;
    }

    /**
     * <p>
     * Hint: use {@link util.PipePatterns}
     * </p>
     *
     * @return the character representation of a termination cell in game
     */
    @Override
    public char toSingleChar() {
        switch (type) {
            case SOURCE:
                switch (pointingTo) {
                    case UP:
                        return isFilled ? PipePatterns.SOURCE_UP_FILLED : PipePatterns.SOURCE_UP;
                    case DOWN:
                        return isFilled ? PipePatterns.SOURCE_DOWN_FILLED : PipePatterns.SOURCE_DOWN;
                    case LEFT:
                        return isFilled ? PipePatterns.SOURCE_LEFT_FILLED : PipePatterns.SOURCE_LEFT;
                    case RIGHT:
                        return isFilled ? PipePatterns.SOURCE_RIGHT_FILLED : PipePatterns.SOURCE_RIGHT;
                }
                break;
            case SINK:
                switch (pointingTo) {
                    case UP:
                        return isFilled ? PipePatterns.SINK_UP_FILLED : PipePatterns.SINK_UP;
                    case DOWN:
                        return isFilled ? PipePatterns.SINK_DOWN_FILLED : PipePatterns.SINK_DOWN;
                    case LEFT:
                        return isFilled ? PipePatterns.SINK_LEFT_FILLED : PipePatterns.SINK_LEFT;
                    case RIGHT:
                        return isFilled ? PipePatterns.SINK_RIGHT_FILLED : PipePatterns.SINK_RIGHT;
                }
                break;
        }
        // Default fallback character if none matched
        return '?';
    }

    /**
     * Represents a source or a sink {@link Cell}.
     */
    public static enum Type {
        SOURCE, SINK
    }
}