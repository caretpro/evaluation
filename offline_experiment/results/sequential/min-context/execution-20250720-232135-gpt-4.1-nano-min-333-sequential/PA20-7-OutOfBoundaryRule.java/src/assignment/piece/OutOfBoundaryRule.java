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
		int width = game.getWidth();
		int height = game.getHeight();
		Position source = move.getSource();
		Position destination = move.getDestination();
		boolean sourceInBounds = source.getX() >= 0 && source.getX() < width && source.getY() >= 0
				&& source.getY() < height;
		boolean destinationInBounds = destination.getX() >= 0 && destination.getX() < width && destination.getY() >= 0
				&& destination.getY() < height;
		return sourceInBounds && destinationInBounds;
	}
}
