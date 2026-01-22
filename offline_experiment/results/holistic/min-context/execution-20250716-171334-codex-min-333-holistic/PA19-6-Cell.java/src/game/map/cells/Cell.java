
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;

/**
 * A wall cell: impassable.
 */
public class WallCell extends Cell {
    public WallCell(@NotNull Coordinate coord) {
        super(coord);
    }
}