
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;

/**
 * Represents a wall in {@link game.map.Map}.
 */
public class WallCell extends Cell {

    public WallCell(@NotNull Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return 'W';
    }
}