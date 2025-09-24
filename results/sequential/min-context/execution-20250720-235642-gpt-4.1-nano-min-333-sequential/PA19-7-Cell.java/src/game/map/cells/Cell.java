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
		switch (c) {
		case 'W':
			return new WallCell(coord);
		case '.':
			return new Cell(coord) {
				@Override
				public char toSingleChar() {
					return '.';
				}
			};
		case '^':
			return new PipeCell(coord, Direction.UP);
		case 'v':
			return new PipeCell(coord, Direction.DOWN);
		case '<':
			return new PipeCell(coord, Direction.LEFT);
		case '>':
			return new PipeCell(coord, Direction.RIGHT);
		default:
			if (terminationType != null) {
				return new TerminationCell(coord, Direction.UP, terminationType);
			}
			return null;
		}
	}
}
