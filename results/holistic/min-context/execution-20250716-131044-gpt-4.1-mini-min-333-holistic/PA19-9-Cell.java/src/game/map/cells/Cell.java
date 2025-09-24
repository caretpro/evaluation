
package game.map.cells;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
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
        return switch (c) {
            case 'W' -> new WallCell(coord);
            case '.' -> new EmptyCell(coord);
            case '^' -> terminationType == null ? null : new TerminationCell(coord, Direction.UP, terminationType);
            case 'v' -> terminationType == null ? null : new TerminationCell(coord, Direction.DOWN, terminationType);
            case '<' -> terminationType == null ? null : new TerminationCell(coord, Direction.LEFT, terminationType);
            case '>' -> terminationType == null ? null : new TerminationCell(coord, Direction.RIGHT, terminationType);
            default -> null;
        };
    }

    /**
     * Returns the character representation of this cell.
     */
    @Override
    public abstract char toSingleChar();

    /**
     * Returns whether a pipe can be placed on this cell.
     * Default is false.
     */
    public boolean canPlacePipe() {
        return false;
    }

    /**
     * Returns whether the pipe can be filled from the given direction.
     * Default is false.
     *
     * @param from Direction from which the pipe is filled.
     * @return true if pipe can be filled from the given direction, false otherwise.
     */
    public boolean canFillPipeFrom(Direction from) {
        return false;
    }

    /**
     * Represents a wall cell in the map.
     */
    public static final class WallCell extends Cell {

        WallCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return 'W';
        }

        @Override
        public boolean canPlacePipe() {
            return false;
        }

        @Override
        public boolean canFillPipeFrom(Direction from) {
            return false;
        }
    }

    /**
     * Represents an empty cell in the map.
     */
    public static final class EmptyCell extends Cell {

        EmptyCell(@NotNull Coordinate coord) {
            super(coord);
        }

        @Override
        public char toSingleChar() {
            return '.';
        }

        @Override
        public boolean canPlacePipe() {
            return true;
        }

        @Override
        public boolean canFillPipeFrom(Direction from) {
            // Empty cells accept pipe filling from any direction
            return true;
        }
    }

    /**
     * Represents a termination cell (source or sink) in the map.
     */
    public static final class TerminationCell extends Cell {

        public enum Type {
            SOURCE,
            SINK
        }

        private final Direction facing;
        private final Type terminationType;

        public TerminationCell(@NotNull Coordinate coord, Direction facing, Type terminationType) {
            super(coord);
            this.facing = facing;
            this.terminationType = terminationType;
        }

        public Direction getFacing() {
            return facing;
        }

        public Type getTerminationType() {
            return terminationType;
        }

        @Override
        public char toSingleChar() {
            return switch (facing) {
                case UP -> '^';
                case DOWN -> 'v';
                case LEFT -> '<';
                case RIGHT -> '>';
            };
        }

        @Override
        public boolean canPlacePipe() {
            // Pipes cannot be placed on termination cells (source/sink)
            return false;
        }

        @Override
        public boolean canFillPipeFrom(Direction from) {
            // A termination cell can only be filled from the direction opposite to its facing direction
            // For example, if facing UP, it can be filled from DOWN
            return from == facing.opposite();
        }
    }
}