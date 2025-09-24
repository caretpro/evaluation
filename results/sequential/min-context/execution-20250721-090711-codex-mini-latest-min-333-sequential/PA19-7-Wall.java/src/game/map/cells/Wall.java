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
	 * <p> Hint: use  {@link util.PipePatterns} </p>
	 * @return  the character representation of a wall in game
	 */
	@Override
	public char toSingleChar() {
		int mask = 0;
		if (PipePatterns.hasWallAt(coord, PipePatterns.NORTH))
			mask |= 1;
		if (PipePatterns.hasWallAt(coord, PipePatterns.EAST))
			mask |= 2;
		if (PipePatterns.hasWallAt(coord, PipePatterns.SOUTH))
			mask |= 4;
		if (PipePatterns.hasWallAt(coord, PipePatterns.WEST))
			mask |= 8;
		return PipePatterns.wallGlyph(mask);
	}
}
