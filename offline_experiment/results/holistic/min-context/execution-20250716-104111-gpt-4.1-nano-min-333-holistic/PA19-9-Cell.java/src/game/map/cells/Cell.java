
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
                return new PipeCell(coord, Direction.UP);
            case 'v':
                return new PipeCell(coord, Direction.DOWN);
            case '<':
                return new PipeCell(coord, Direction.LEFT);
            case '>':
                return new PipeCell(coord, Direction.RIGHT);
            default:
                if (terminationType != null) {
                    return new TerminationCell(coord, terminationType);
                }
                return null;
        }
    }

    @Override
    public abstract char toSingleChar();

    static class WallCell extends Cell {
        WallCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return 'W';
        }
    }

    static class EmptyCell extends Cell {
        EmptyCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return '.';
        }
    }

    static class PipeCell extends Cell {
        @NotNull
        private final Direction direction;

        PipeCell(@NotNull Coordinate coord, @NotNull Direction direction) {
            super(coord);
            this.direction = direction;
        }

        @Override
        public char toSingleChar() {
            return switch (direction) {
                case UP -> '^';
                case DOWN -> 'v';
                case LEFT -> '<';
                case RIGHT -> '>';
            };
        }
    }

    static class TerminationCell extends Cell {
        enum Type {
            SOURCE,
            SINK
        }

        @NotNull
        private final Type terminationType;

        TerminationCell(@NotNull Coordinate coord, @NotNull Type terminationType) {
            super(coord);
            this.terminationType = terminationType;
        }

        @Override
        public char toSingleChar() {
            return switch (terminationType) {
                case SOURCE -> 'S';
                case SINK -> 'K';
            };
        }
    }
}