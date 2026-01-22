
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
    public static Cell fromChar(char c, @NotNull Coordinate coord, @Nullable TerminationCell.Type terminationType) {
        switch (c) {
            case 'W':
                return new WallCell(coord);
            case '.':
                return new EmptyCell(coord);
            case '^':
                return new TerminationCell(coord, 
                    (terminationType != null) ? terminationType : TerminationCell.Type.SOURCE, 
                    Direction.UP);
            case 'v':
                return new TerminationCell(coord, 
                    (terminationType != null) ? terminationType : TerminationCell.Type.SOURCE, 
                    Direction.DOWN);
            case '<':
                return new TerminationCell(coord, 
                    (terminationType != null) ? terminationType : TerminationCell.Type.SOURCE, 
                    Direction.LEFT);
            case '>':
                return new TerminationCell(coord, 
                    (terminationType != null) ? terminationType : TerminationCell.Type.SOURCE, 
                    Direction.RIGHT);
            default:
                return null;
        }
    }
}

/**
 * Example subclass implementations (assuming they exist).
 */

class WallCell extends Cell {
    WallCell(@NotNull Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return 'W';
    }
}

class EmptyCell extends Cell {
    EmptyCell(@NotNull Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return '.';
    }
}

class TerminationCell extends Cell {
    enum Type {
        SOURCE,
        SINK
    }

    private final Type terminationType;
    private final Direction direction;

    TerminationCell(@NotNull Coordinate coord, @NotNull Type terminationType, @NotNull Direction direction) {
        super(coord);
        this.terminationType = terminationType;
        this.direction = direction;
    }

    @Override
    public char toSingleChar() {
        switch (direction) {
            case UP:
                return '^';
            case DOWN:
                return 'v';
            case LEFT:
                return '<';
            case RIGHT:
                return '>';
            default:
                return '?';
        }
    }
}