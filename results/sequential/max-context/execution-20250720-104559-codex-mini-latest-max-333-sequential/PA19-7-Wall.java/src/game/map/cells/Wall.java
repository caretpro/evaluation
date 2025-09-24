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
	public Wall(Coordinate coord) {
		super(coord);
		this.pattern = PipePatterns.of(false, false, false, false);
	}

	protected Pipe(@NotNull Coordinate coord, PipePatterns.Pattern pattern) {
		super(coord);
		this.pattern = pattern;
	}
}
