
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
    public final TerminationCell.Type type;

    /**
     *
     * @param coord coordination of this cell
     * @param direction direction of this termination
     * @param type type of this termination
     */
    public TerminationCell(@NotNull Coordinate coord, @NotNull Direction direction, @NotNull TerminationCell.Type type) {
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
        if (type == TerminationCell.Type.SOURCE) {
            switch (pointingTo) {
                case UP:
                    return PipePatterns.SOURCE_UP;
                case DOWN:
                    return PipePatterns.SOURCE_DOWN;
                case LEFT:
                    return PipePatterns.SOURCE_LEFT;
                case RIGHT:
                    return PipePatterns.SOURCE_RIGHT;
            }
        } else { // Type.SINK
            switch (pointingTo) {
                case UP:
                    return PipePatterns.SINK_UP;
                case DOWN:
                    return PipePatterns.SINK_DOWN;
                case LEFT:
                    return PipePatterns.SINK_LEFT;
                case RIGHT:
                    return PipePatterns.SINK_RIGHT;
            }
        }
        // Fallback character if none matched
        return '?';
    }

    /**
     * Enum for the type of termination.
     */
    public enum Type {
        SOURCE, SINK
    }
}