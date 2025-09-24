
package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.PipePatterns;

/**
 * Represents a wall in {@link game.map.Map}.
 */
public class Wall extends Cell {

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
		return 'W';
	}

	/**
	 * @param coord  coordination of  {@code  this}  cell
	 */
	public Wall(@NotNull Coordinate coord) {
		super(coord);
	}
}