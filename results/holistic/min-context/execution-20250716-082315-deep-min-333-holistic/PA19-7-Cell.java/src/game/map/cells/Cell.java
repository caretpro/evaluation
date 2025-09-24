
package game.map.cells;

import game.MapElement;
import game.map.cells.FillableCell;
import game.map.cells.TerminationCell;
import game.map.cells.WallCell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

/**
 * Representation of a cell in the {@link game.map.Map}.
 */
public abstract class Cell implements MapElement {

    @NotNull
    public final Coordinate coord;

    Cell(@NotNull Coordinate coord) {
        this.coord = coord;
    }

    public static Cell fromChar(char c, Coordinate coord, TerminationCell.Type terminationType) {
        switch (c) {
            case 'W':
                return new WallCell(coord);
            case '.':
                return new FillableCell(coord);
            case '^':
                if (terminationType == null) return null;
                return new TerminationCell(coord, Direction.UP, terminationType);
            case 'v':
                if (terminationType == null) return null;
                return new TerminationCell(coord, Direction.DOWN, terminationType);
            case '<':
                if (terminationType == null) return null;
                return new TerminationCell(coord, Direction.LEFT, terminationType);
            case '>':
                if (terminationType == null) return null;
                return new TerminationCell(coord, Direction.RIGHT, terminationType);
            default:
                return null;
        }
    }
}