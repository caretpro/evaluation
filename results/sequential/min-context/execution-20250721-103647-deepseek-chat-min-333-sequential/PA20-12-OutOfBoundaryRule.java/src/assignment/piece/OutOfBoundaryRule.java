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
		int size = game.size();
		return move.fromX() >= 0 && move.fromX() < size && move.fromY() >= 0 && move.fromY() < size && move.toX() >= 0
				&& move.toX() < size && move.toY() >= 0 && move.toY() < size;
	}
}
