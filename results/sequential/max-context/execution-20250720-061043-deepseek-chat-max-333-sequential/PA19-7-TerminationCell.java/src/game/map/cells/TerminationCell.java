
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;
import util.PipePatterns;

public class TerminationCell extends Cell {
    private boolean isFilled = false;
    @NotNull public final Direction pointingTo;
    @NotNull public final Type type;

    public TerminationCell(@NotNull Coordinate coord, @NotNull Direction direction, @NotNull Type type) {
        super(coord);
        this.pointingTo = direction;
        this.type = type;
    }

    @Override
    public char toSingleChar() {
        switch (pointingTo) {
            case UP: return type == Type.SOURCE ? '^' : 'v';
            case DOWN: return type == Type.SOURCE ? 'v' : '^';
            case LEFT: return type == Type.SOURCE ? '<' : '>';
            case RIGHT: return type == Type.SOURCE ? '>' : '<';
            default: throw new IllegalStateException("Unknown direction");
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

    public void setFilled() {
        this.isFilled = true;
    }
}