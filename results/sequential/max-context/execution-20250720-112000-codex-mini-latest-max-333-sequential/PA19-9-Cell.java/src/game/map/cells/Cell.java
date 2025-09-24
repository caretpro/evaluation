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
	public static Cell fromChar(char c, @NotNull Coordinate coord, @Nullable TerminationCell.Type terminationType) {
		return switch (c) {
		case 'W':
			yield new WallCell(coord);
		case '.':
			yield new PlainCell(coord);
		case '^': {
			if (terminationType == null)
				yield null;
			yield new TerminationCell(coord, Direction.UP, terminationType);
		}
		case 'v': {
			if (terminationType == null)
				yield null;
			yield new TerminationCell(coord, Direction.DOWN, terminationType);
		}
		case '<': {
			if (terminationType == null)
				yield null;
			yield new TerminationCell(coord, Direction.LEFT, terminationType);
		}
		case '>': {
			if (terminationType == null)
				yield null;
			yield new TerminationCell(coord, Direction.RIGHT, terminationType);
		}
		default:
			yield null;
		};
	}
}
