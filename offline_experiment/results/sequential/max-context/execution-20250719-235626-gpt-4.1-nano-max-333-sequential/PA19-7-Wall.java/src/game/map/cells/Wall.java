
package game.map.cells;

import util.Coordinate;

/**
 * Represents a wall in {@link game.map.Map}.
 */
public class Wall extends Cell {

    /**
     * Constructor that initializes a Wall with a specific coordinate.
     *
     * @param coord the coordinate of the wall
     */
    public Wall(@NotNull Coordinate coord) {
        super(coord);
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
        return 'W';
    }
}