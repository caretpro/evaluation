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
		Place source = move.getSource();
		Place destination = move.getDestination();
		int sourceRow = source.getRow();
		int sourceCol = source.getCol();
		int destinationRow = destination.getRow();
		int destinationCol = destination.getCol();
		if (sourceRow < 0 || sourceRow >= boardSize || sourceCol < 0 || sourceCol >= boardSize || destinationRow < 0
				|| destinationRow >= boardSize || destinationCol < 0 || destinationCol >= boardSize) {
			return false;
		}
		return true;
	}
}
