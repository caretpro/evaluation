package game.map.cells;

import org.jetbrains.annotations.NotNull;
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
	 * mask: bit0=UP, bit1=RIGHT, bit2=DOWN, bit3=LEFT 
	 */
	public static char wallChar(int mask) {
		return WALL_CHARS[mask & 0xF];
	}
}
