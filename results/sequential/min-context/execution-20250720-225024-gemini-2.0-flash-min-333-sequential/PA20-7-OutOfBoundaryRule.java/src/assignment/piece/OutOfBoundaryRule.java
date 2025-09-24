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
		int numRows = game.getRows();
		int numCols = game.getCols();
		int sourceRow = move.getSource().getRow();
		int sourceCol = move.getSource().getCol();
		int destRow = move.getDestination().getRow();
		int destCol = move.getDestination().getCol();
		if (sourceRow < 0 || sourceRow >= numRows || sourceCol < 0 || sourceCol >= numCols) {
			return false;
		}
		if (destRow < 0 || destRow >= numRows || destCol < 0 || destCol >= numCols) {
			return false;
		}
		return true;
	}
}
