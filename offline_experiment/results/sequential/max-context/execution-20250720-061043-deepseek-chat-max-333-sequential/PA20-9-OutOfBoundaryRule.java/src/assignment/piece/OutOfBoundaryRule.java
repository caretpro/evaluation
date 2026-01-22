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
		assignment.protocol.Place source = move.getSource();
		assignment.protocol.Place destination = move.getDestination();
		boolean sourceValid = source.x() >= 0 && source.x() < boardSize && source.y() >= 0 && source.y() < boardSize;
		boolean destinationValid = destination.x() >= 0 && destination.x() < boardSize && destination.y() >= 0
				&& destination.y() < boardSize;
		return sourceValid && destinationValid;
	}
}
