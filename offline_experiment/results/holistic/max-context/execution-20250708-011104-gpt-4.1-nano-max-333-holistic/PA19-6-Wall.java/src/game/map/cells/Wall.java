
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;

/**
 * Represents a wall in {@link game.map.Map}.
 */
public class Wall extends Cell {

    /**
     * Constructs a Wall cell at the given coordinate.
     *
     * @param coord coordination of {@code this} cell
     */
    public Wall(@NotNull Coordinate coord) {
        super(coord); // Explicitly invoke superclass constructor
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