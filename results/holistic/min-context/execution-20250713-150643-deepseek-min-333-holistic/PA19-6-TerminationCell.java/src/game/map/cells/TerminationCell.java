
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;
import util.PipePatterns;

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
        return type == Type.SOURCE ? 'S' : 'K';
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