
package game.map.cells;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;

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
     * W: WallCell
     * .: CorridorCell
     * ^: TerminationCell of type NORTH
     * v: TerminationCell of type SOUTH
     * <: TerminationCell of type WEST
     * >: TerminationCell of type EAST
     * If the character does not represent a {@link TerminationCell}, the {@code terminationType} parameter can be ignored.
     * </p>
     *
     * @param c Character to parse. For example, 'W' refers to a wall.
     * @param coord Coordinate of the newly created cell.
     * @param terminationType If the character is a termination cell, its type. Otherwise, this argument is ignored and
     *                        can be null.
     * @return A cell based on the given creation parameters, or null if the parameters cannot form a valid Cell.
     */
    @Nullable
    public static Cell fromChar(char c, @NotNull Coordinate coord, @Nullable TerminationCell.Type terminationType) {
        switch (c) {
            case 'W':
                return new WallCell(coord);
            case '.':
                return new CorridorCell(coord);
            case '^':
                return new TerminationCell(coord, TerminationCell.Type.NORTH);
            case 'v':
                return new TerminationCell(coord, TerminationCell.Type.SOUTH);
            case '<':
                return new TerminationCell(coord, TerminationCell.Type.WEST);
            case '>':
                return new TerminationCell(coord, TerminationCell.Type.EAST);
            default:
                return null;
        }
    }
}