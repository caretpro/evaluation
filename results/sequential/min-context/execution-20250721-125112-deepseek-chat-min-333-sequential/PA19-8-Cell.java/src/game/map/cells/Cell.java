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
			return new game.map.cells.WallCell(coord);
		case '.':
			return new game.map.cells.FillableCell(coord);
		case '^':
			return terminationType != null ? new TerminationCell(coord, terminationType, Direction.UP) : null;
		case 'v':
			return terminationType != null ? new TerminationCell(coord, terminationType, Direction.DOWN) : null;
		case '<':
			return terminationType != null ? new TerminationCell(coord, terminationType, Direction.LEFT) : null;
		case '>':
			return terminationType != null ? new TerminationCell(coord, terminationType, Direction.RIGHT) : null;
		default:
			return null;
		}
	}
}
