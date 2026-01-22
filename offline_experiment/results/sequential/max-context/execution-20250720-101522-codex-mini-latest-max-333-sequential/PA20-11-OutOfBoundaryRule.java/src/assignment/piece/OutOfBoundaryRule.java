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
		int sx = move.getSource().x();
		int sy = move.getSource().y();
		int dx = move.getDestination().x();
		int dy = move.getDestination().y();
		return sx >= 0 && sx < size && sy >= 0 && sy < size && dx >= 0 && dx < size && dy >= 0 && dy < size;
	}
}
