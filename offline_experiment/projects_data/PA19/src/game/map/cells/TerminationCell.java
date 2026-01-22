package game.map.cells;

import game.map.cells.Cell;
import util.Coordinate;
import util.Direction;
import util.PipePatterns;

/**
 * Represents a source or a sink {@link Cell}.
 */
public class TerminationCell extends Cell {

    private boolean isFilled = false;
    public final Direction pointingTo;
    public final Type type;

    /**
     *
     * @param coord coordination of this cell
     * @param direction direction of this termination
     * @param type type of this termination
     */
    public TerminationCell(Coordinate coord, Direction direction, Type type) {
        // TODO
    }

    /**
     * Sets this cell as filled.
     */
    public void setFilled() {
        // TODO
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
        // TODO
        return '\0';
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

        public CreateInfo(Coordinate coord, Direction dir) {
            this.coord = coord;
            this.dir = dir;
        }
    }
}
