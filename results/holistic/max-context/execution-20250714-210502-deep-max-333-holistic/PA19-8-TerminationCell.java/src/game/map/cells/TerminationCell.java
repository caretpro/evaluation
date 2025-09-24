
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;

/**
 * Represents a source or a sink {@link Cell}.
 */
public class TerminationCell extends Cell {

    private boolean isFilled = false;
    @NotNull
    public final Direction pointingTo;
    @NotNull
    public final Type type;

    public TerminationCell(Coordinate coord, Direction direction, Type type) {
        super(coord);
        this.pointingTo = direction;
        this.type = type;
    }

    public void setFilled() {
        this.isFilled = true;
    }

    @Override
    public char toSingleChar() {
        if (isFilled) {
            switch (pointingTo) {
                case UP: return '▲';
                case DOWN: return '▼';
                case LEFT: return '◄';
                case RIGHT: return '►';
                default: throw new IllegalStateException("Unknown direction");
            }
        } else {
            switch (pointingTo) {
                case UP: return '△';
                case DOWN: return '▽';
                case LEFT: return '◁';
                case RIGHT: return '▷';
                default: throw new IllegalStateException("Unknown direction");
            }
        }
    }

    public enum Type {
        SOURCE, SINK
    }

    public static class CreateInfo {
        public final Coordinate coord;
        public final Direction dir;

        public CreateInfo(@NotNull Coordinate coord, @NotNull Direction dir) {
            this.coord = coord;
            this.dir = dir;
        }
    }
}