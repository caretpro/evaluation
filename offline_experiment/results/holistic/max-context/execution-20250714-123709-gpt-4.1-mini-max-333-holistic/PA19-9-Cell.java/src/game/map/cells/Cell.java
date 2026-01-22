
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
     * Types of termination cells.
     */
    public enum Type {
        SOURCE,
        SINK
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
    public static Cell fromChar(char c, Coordinate coord, Type terminationType) {
        return switch (c) {
            case 'W' -> new WallCell(coord);
            case '.' -> new NormalCell(coord);
            case '^' -> terminationType != null ? new TerminationCell(coord, Direction.UP, terminationType) : null;
            case 'v' -> terminationType != null ? new TerminationCell(coord, Direction.DOWN, terminationType) : null;
            case '<' -> terminationType != null ? new TerminationCell(coord, Direction.LEFT, terminationType) : null;
            case '>' -> terminationType != null ? new TerminationCell(coord, Direction.RIGHT, terminationType) : null;
            default -> null;
        };
    }

    /**
     * Wall cell implementation.
     */
    private static final class WallCell extends Cell {
        WallCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return 'W';
        }
    }

    /**
     * Normal cell implementation.
     */
    private static final class NormalCell extends Cell {
        NormalCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return '.';
        }
    }

    /**
     * Termination cell implementation.
     */
    public static final class TerminationCell extends Cell {

        @NotNull
        public final Direction direction;

        @NotNull
        public final Type type;

        public TerminationCell(@NotNull Coordinate coord, @NotNull Direction direction, @NotNull Type type) {
            super(coord);
            this.direction = direction;
            this.type = type;
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
}