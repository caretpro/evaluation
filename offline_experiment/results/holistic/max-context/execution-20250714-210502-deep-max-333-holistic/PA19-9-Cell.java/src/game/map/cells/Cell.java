
package game.map.cells;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import util.Coordinate;

/**
 * Represents a wall cell in the game map.
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