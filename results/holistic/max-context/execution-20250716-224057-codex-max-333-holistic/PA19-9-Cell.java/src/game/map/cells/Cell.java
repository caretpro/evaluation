
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
     * .: Cell
     * ^: Source/Sink pipe pointing upward
     * v: Source/Sink pipe pointing downward
     * <: Source/Sink pipe pointing leftward
     * >: Source/Sink pipe pointing rightward
     * If the character does not represent a {@link TerminationCell}, the {@code terminationType} parameter can be ignored.
     * </p>
     *
     * @param c Character to parse. For example, 'W' refers to a wall.
     * @param coord Coordinate of the newly created cell.
     * @param terminationType If the character is a termination cell, its type. Otherwise, this argument is ignored and
     *                        can be null.
     * @return A cell based on the given creation parameters, or null if the parameters cannot form a valid Cell.
     */
    public static Cell fromChar(char c, Coordinate coord, @Nullable TerminationCell.Type terminationType) {
        return switch (c) {
            case 'W'       -> new Wall(coord);
            case '.'       -> new Empty(coord);
            case '^'       -> new Pipe(coord, Direction.UP);
            case 'v'       -> new Pipe(coord, Direction.DOWN);
            case '<'       -> new Pipe(coord, Direction.LEFT);
            case '>'       -> new Pipe(coord, Direction.RIGHT);
            case 'S', 's'  -> TerminationCell.source(coord, terminationType);
            case 'E', 'e'  -> TerminationCell.sink(coord, terminationType);
            default        -> null;
        };
    }
}