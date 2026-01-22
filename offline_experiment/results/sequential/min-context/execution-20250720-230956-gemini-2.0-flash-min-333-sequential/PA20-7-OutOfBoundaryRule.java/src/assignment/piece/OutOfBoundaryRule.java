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
		int boardSize = game.getBoard().length;
		int source = move.getSource().getIndex();
		int destination = move.getDestination().getIndex();
		int sourceRow = source / boardSize;
		int sourceCol = source % boardSize;
		int destinationRow = destination / boardSize;
		int destinationCol = destination % boardSize;
		if (sourceRow < 0 || sourceRow >= boardSize || sourceCol < 0 || sourceCol >= boardSize || destinationRow < 0
				|| destinationRow >= boardSize || destinationCol < 0 || destinationCol >= boardSize) {
			return false;
		}
		return true;
	}
}
