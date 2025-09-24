package game.map.cells;

import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.PipePatterns;

/**
 * Represents a wall in {@link game.map.Map}.
 */
public class Wall extends Cell {

	/**
	 * @param coord  coordination of  {@code  this}  cell
	 */
	public Wall(@NotNull Coordinate coord) {
		super(coord, PipePatterns.WALL);
	}

	@Override
	public char toSingleChar() {
		return PipePatterns.WALL;
	}
}
