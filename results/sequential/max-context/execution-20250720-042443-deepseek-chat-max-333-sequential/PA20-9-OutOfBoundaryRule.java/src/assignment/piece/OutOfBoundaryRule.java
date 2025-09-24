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
		int size = game.getConfiguration().getSize();
		assignment.protocol.Place source = move.getSource();
		assignment.protocol.Place destination = move.getDestination();
		boolean sourceValid = source.x() >= 0 && source.x() < size && source.y() >= 0 && source.y() < size;
		boolean destinationValid = destination.x() >= 0 && destination.x() < size && destination.y() >= 0
				&& destination.y() < size;
		return sourceValid && destinationValid;
	}
}
