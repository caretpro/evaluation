package game.map.cells;

import game.map.cells.Cell;
import util.Coordinate;
import util.PipePatterns;

/**
 * Represents a wall in {@link game.map.Map}.
 */
public class Wall extends Cell {

    /**
     * @param coord coordination of {@code this} cell
     */
    public Wall(Coordinate coord) {
        // TODO
    }

    /**
     * <p>
     * Hint: use {@link util.PipePatterns}
     * </p>
     *
     * @return the character representation of a wall in game
     */
    @Override
    public char toSingleChar() {
        // TODO
        return '\0';
    }
}
