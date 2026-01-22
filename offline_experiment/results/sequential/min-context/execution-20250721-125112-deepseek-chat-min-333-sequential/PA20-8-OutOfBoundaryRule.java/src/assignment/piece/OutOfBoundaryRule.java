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
		int size = game.size;
		return move.source.x >= 0 && move.source.x < size && move.source.y >= 0 && move.source.y < size
				&& move.destination.x >= 0 && move.destination.x < size && move.destination.y >= 0
				&& move.destination.y < size;
	}
}
