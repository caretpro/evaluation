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

	public static Cell fromChar(char c, Coordinate coord, TerminationCell.Type terminationType) {
		return switch (c) {
		case 'W':
			yield new Wall(coord);
		case '.':
			yield new Plain(coord);
		case '^':
			yield terminationType != null ? new TerminationCell(coord, Direction.UP, terminationType) : null;
		case 'v':
			yield terminationType != null ? new TerminationCell(coord, Direction.DOWN, terminationType) : null;
		case '<':
			yield terminationType != null ? new TerminationCell(coord, Direction.LEFT, terminationType) : null;
		case '>':
			yield terminationType != null ? new TerminationCell(coord, Direction.RIGHT, terminationType) : null;
		default:
			yield null;
		};
	}
}
