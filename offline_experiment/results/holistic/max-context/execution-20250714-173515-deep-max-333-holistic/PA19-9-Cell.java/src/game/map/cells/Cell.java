
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
    public static Cell fromChar(char c, Coordinate coord, TerminationCell.Type terminationType) {
        switch (Character.toUpperCase(c)) {
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

    public static class WallCell extends Cell {
        public WallCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return 'W';
        }
    }

    public static class FillableCell extends Cell {
        public FillableCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return '.';
        }
    }

    public static class TerminationCell extends Cell {
        public enum Type { SOURCE, SINK }

        public final Direction dir;
        public final Type type;

        public TerminationCell(@NotNull Coordinate coord, @NotNull Direction dir, @NotNull Type type) {
            super(coord);
            this.dir = dir;
            this.type = type;
        }

        @Override
        public char toSingleChar() {
            return switch (dir) {
                case UP -> '^';
                case DOWN -> 'v';
                case LEFT -> '<';
                case RIGHT -> '>';
            };
        }
    }
}