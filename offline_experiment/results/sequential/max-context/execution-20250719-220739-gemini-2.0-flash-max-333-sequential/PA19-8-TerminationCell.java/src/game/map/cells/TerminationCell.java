
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
			return new Wall(coord);
		case '.':
			return new EmptyCell(coord);
		case '^':
			if (terminationType != null) {
				return new TerminationCell(coord, Direction.UP, terminationType);
			}
			break;
		case 'v':
			if (terminationType != null) {
				return new TerminationCell(coord, Direction.DOWN, terminationType);
			}
			break;
		case '<':
			if (terminationType != null) {
				return new TerminationCell(coord, Direction.LEFT, terminationType);
			}
			break;
		case '>':
			if (terminationType != null) {
				return new TerminationCell(coord, Direction.RIGHT, terminationType);
			}
			break;
		default:
			return null;
		}
		return null;
	}
}