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
		super(coord);
	}

	@Override
	public char toSingleChar() {
		boolean up = MapElement.getMap().cellAt(coord.add(PipePatterns.WALL_UP)).map(c -> c instanceof Wall)
				.orElse(false);
		boolean right = MapElement.getMap().cellAt(coord.add(PipePatterns.WALL_RIGHT)).map(c -> c instanceof Wall)
				.orElse(false);
		boolean down = MapElement.getMap().cellAt(coord.add(PipePatterns.WALL_DOWN)).map(c -> c instanceof Wall)
				.orElse(false);
		boolean left = MapElement.getMap().cellAt(coord.add(PipePatterns.WALL_LEFT)).map(c -> c instanceof Wall)
				.orElse(false);
		return PipePatterns.wallChar(up, right, down, left);
	}
}
