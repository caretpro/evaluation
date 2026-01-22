
package game.map.cells;

import game.MapElement;
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

    /**
     * Parses a {@link Cell} from a character.
     *
     * <p>
     * Here is the list of characters to their corresponding map element:
     * W: Wall
     * .: Plain cell
     * ^: Pipe pointing upward
     * v: Pipe pointing downward
     * <: Pipe pointing leftward
     * >: Pipe pointing rightward
     * S: Source termination
     * T: Sink termination
     * </p>
     *
     * @param c Character to parse. For example, 'W' refers to a wall.
     * @param coord Coordinate of the newly created cell.
     * @param terminationType If the character is a termination cell, its type.
     *                        Otherwise, this argument is ignored and can be null.
     * @return A cell based on the given creation parameters, or null if the parameters cannot form a valid Cell.
     */
    @Nullable
    public static Cell fromChar(char c,
                                @NotNull Coordinate coord,
                                @Nullable TerminationCell.Type terminationType) {
        switch (c) {
            case 'W':
                return new WallCell(coord);
            case '.':
                return new PlainCell(coord);
            case '^':
                return new PipeCell(coord, Direction.UP);
            case 'v':
                return new PipeCell(coord, Direction.DOWN);
            case '<':
                return new PipeCell(coord, Direction.LEFT);
            case '>':
                return new PipeCell(coord, Direction.RIGHT);
            case 'S':
                if (terminationType == TerminationCell.Type.SOURCE) {
                    return new TerminationCell(coord, Direction.UP, TerminationCell.Type.SOURCE);
                }
                return null;
            case 'T':
                if (terminationType == TerminationCell.Type.SINK) {
                    return new TerminationCell(coord, Direction.DOWN, TerminationCell.Type.SINK);
                }
                return null;
            default:
                return null;
        }
    }
}