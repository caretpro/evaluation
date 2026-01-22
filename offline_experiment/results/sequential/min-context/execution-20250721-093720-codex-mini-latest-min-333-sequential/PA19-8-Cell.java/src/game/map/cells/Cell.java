package game.map.cells;

import game.MapElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

/**
 * Representation of a cell in the {@link game.map.Map}.
 */
public abstract class Cell implements MapElement {

	@NotNull
	public final Coordinate coord;

	Cell(@NotNull Coordinate coord) {
		this.coord = coord;
	}

	@Nullable
	public static Cell fromChar(char c, Coordinate coord, TerminationCell.Type terminationType) {
		switch (c) {
		case 'W':
			return new Wall(coord);
		case '.':
			return new Pipe(coord);
		case '^':
			return new TerminationCell(coord, Direction.UP, terminationType);
		case 'v':
			return new TerminationCell(coord, Direction.DOWN, terminationType);
		case '<':
			return new TerminationCell(coord, Direction.LEFT, terminationType);
		case '>':
			return new TerminationCell(coord, Direction.RIGHT, terminationType);
		default:
			return null;
		}
	}
}
