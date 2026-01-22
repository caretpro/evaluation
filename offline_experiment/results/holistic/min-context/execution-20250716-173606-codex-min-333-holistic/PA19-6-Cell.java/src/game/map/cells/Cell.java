
package game.map.cells;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

// Add these imports so WallCell, EmptyCell and PipeCell are visible:
import game.map.cells.WallCell;
import game.map.cells.EmptyCell;
import game.map.cells.PipeCell;

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
     * .: Empty cell
     * ^: Source/Sink pipe pointing upward
     * v: Source/Sink pipe pointing downward
     * <: Source/Sink pipe pointing leftward
     * >: Source/Sink pipe pointing rightward
     * If the character does not represent a {@link TerminationCell}, the {@code terminationType} parameter can be ignored.
     * </p>
     *
     * @param c               Character to parse. For example, 'W' refers to a wall.
     * @param coord           Coordinate of the newly created cell.
     * @param terminationType If the character is a termination cell, its type. Otherwise, this argument is ignored and
     *                        can be null.
     * @return A cell based on the given creation parameters, or null if the parameters cannot form a valid Cell.
     */
    @Nullable
    public static Cell fromChar(char c, Coordinate coord, @Nullable TerminationCell.Type terminationType) {
        switch (c) {
            case 'W':
                return new WallCell(coord);
            case '.':
                return new EmptyCell(coord);
            case '^':
                return new PipeCell(coord, Direction.UP, terminationType);
            case 'v':
                return new PipeCell(coord, Direction.DOWN, terminationType);
            case '<':
                return new PipeCell(coord, Direction.LEFT, terminationType);
            case '>':
                return new PipeCell(coord, Direction.RIGHT, terminationType);
            default:
                return null;
        }
    }
}