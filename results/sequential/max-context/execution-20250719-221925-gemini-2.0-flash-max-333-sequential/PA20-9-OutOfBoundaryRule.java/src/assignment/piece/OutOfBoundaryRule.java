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
		int boardSize = game.getConfiguration().getSize();
		int sourceX = move.getSource().x();
		int sourceY = move.getSource().y();
		int destinationX = move.getDestination().x();
		int destinationY = move.getDestination().y();
		if (sourceX < 0 || sourceX >= boardSize || sourceY < 0 || sourceY >= boardSize) {
			return false;
		}
		if (destinationX < 0 || destinationX >= boardSize || destinationY < 0 || destinationY >= boardSize) {
			return false;
		}
		return true;
	}
}
