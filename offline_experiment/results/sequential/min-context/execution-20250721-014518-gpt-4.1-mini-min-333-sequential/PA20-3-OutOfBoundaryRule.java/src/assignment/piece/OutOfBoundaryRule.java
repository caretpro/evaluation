package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
	@Override
	public String getDescription() {
		return "place is out of boundary of gameboard";
	}

	@Override
	public boolean validate(Game game, Move move) {
		int[] size = game.getSize();
		int rows = size[0];
		int columns = size[1];
		int sourceRow = move.getSource().getRowIndex();
		int sourceColumn = move.getSource().getColumnIndex();
		int destRow = move.getDestination().getRowIndex();
		int destColumn = move.getDestination().getColumnIndex();
		boolean sourceInBounds = sourceRow >= 0 && sourceRow < rows && sourceColumn >= 0 && sourceColumn < columns;
		boolean destInBounds = destRow >= 0 && destRow < rows && destColumn >= 0 && destColumn < columns;
		return sourceInBounds && destInBounds;
	}
}
