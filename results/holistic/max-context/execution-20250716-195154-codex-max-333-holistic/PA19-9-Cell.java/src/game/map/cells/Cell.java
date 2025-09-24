
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;

/**
 * An empty (floor) cell that can be traversed.
 */
public class EmptyCell extends Cell {

    public EmptyCell(@NotNull Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return '.';
    }
}