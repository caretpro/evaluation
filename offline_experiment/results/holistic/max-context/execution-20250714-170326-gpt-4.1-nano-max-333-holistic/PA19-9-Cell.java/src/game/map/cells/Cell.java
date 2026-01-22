
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
        switch (c) {
            case 'W':
                return new WallCell(coord);
            case '.':
                return new EmptyCell(coord);
            case '^':
                return new TerminationCell(coord, TerminationCell.Type.UP);
            case 'v':
                return new TerminationCell(coord, TerminationCell.Type.DOWN);
            case '<':
                return new TerminationCell(coord, TerminationCell.Type.LEFT);
            case '>':
                return new TerminationCell(coord, TerminationCell.Type.RIGHT);
            default:
                return null;
        }
    }

    // Define WallCell class
    public static class WallCell extends Cell {
        public WallCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return 'W';
        }
    }

    // Define EmptyCell class
    public static class EmptyCell extends Cell {
        public EmptyCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return '.';
        }
    }

    // Define TerminationCell class
    public static class TerminationCell extends Cell {
        public enum Type {
            UP,
            DOWN,
            LEFT,
            RIGHT
        }

        private final Type type;

        public TerminationCell(@NotNull Coordinate coord, @NotNull Type type) {
            super(coord);
            this.type = type;
        }

        @Override
        public char toSingleChar() {
            switch (type) {
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
}