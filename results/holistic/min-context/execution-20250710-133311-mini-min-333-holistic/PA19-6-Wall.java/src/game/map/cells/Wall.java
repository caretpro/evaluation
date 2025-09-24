
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.PipePatterns;

/**
 * Represents a wall in {@link game.map.Map}.
 */
public class Wall extends Cell {

	/**
	 * Default constructor for deserialization or reflective instantiation.
	 * Calls super with a default coordinate (0,0).
	 */
	public Wall() {
		super(new Coordinate(0, 0));
	}

	/**
	 * @param coord coordination of {@code this} cell
	 */
	public Wall(Coordinate coord) {
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
		return PipePatterns.WALL;
	}
}