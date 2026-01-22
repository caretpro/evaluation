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
		int width = game.width;
		int height = game.height;
		int srcRow = move.getSource().row;
		int srcCol = move.getSource().column;
		int destRow = move.getDestination().row;
		int destCol = move.getDestination().column;
		boolean sourceInside = srcRow >= 0 && srcRow < height && srcCol >= 0 && srcCol < width;
		boolean destinationInside = destRow >= 0 && destRow < height && destCol >= 0 && destCol < width;
		return sourceInside && destinationInside;
	}
}
