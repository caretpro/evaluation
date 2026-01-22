
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
     * @param coord     coordination of this cell
     * @param direction direction of this termination
     * @param type      type of this termination
     */
    public TerminationCell(Coordinate coord, Direction direction, Type type) {
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
        return PipePatterns.getTerminationPatternChar(type, pointingTo, isFilled);
    }

    public enum Type {
        SOURCE, SINK
    }

    /**
     * Data class encapsulating the coordinate and direction of the {@link TerminationCell}.
     */
    public static class CreateInfo {

        public final Coordinate coord;
        public final Direction dir;

        public CreateInfo(@NotNull Coordinate coord, @NotNull Direction dir) {
            this.coord = coord;
            this.dir = dir;
        }
    }
}