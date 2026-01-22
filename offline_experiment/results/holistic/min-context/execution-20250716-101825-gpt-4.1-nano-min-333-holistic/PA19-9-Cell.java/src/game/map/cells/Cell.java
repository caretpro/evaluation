
package game.map.cells;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

/**
 * Represents a pipe cell with a direction and optional termination type.
 */
public class PipeCell extends TerminationCell {
    private final Direction direction;

    public PipeCell(@NotNull Coordinate coord, Direction direction, @Nullable TerminationCell.Type terminationType) {
        super(coord, terminationType);
        this.direction = direction;
    }

    @Override
    public char toSingleChar() {
        return switch (direction) {
            case UP -> '^';
            case DOWN -> 'v';
            case LEFT -> '<';
            case RIGHT -> '>';
        };
    }
}