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
		int boardSize = game.getBoardSize();
		int sourceRow = move.getSourceRow();
		int sourceCol = move.getSourceCol();
		int destRow = move.getDestinationRow();
		int destCol = move.getDestinationCol();
		if (sourceRow < 0 || sourceRow >= boardSize || sourceCol < 0 || sourceCol >= boardSize) {
			return false;
		}
		if (destRow < 0 || destRow >= boardSize || destCol < 0 || destCol >= boardSize) {
			return false;
		}
		return true;
	}
}
