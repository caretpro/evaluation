
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

	@Override
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Knight)) {
			return false;
		}
		Place source = move.getSource();
		Place destination = move.getDestination();

		int deltaX = Math.abs(destination.x() - source.x());
		int deltaY = Math.abs(destination.y() - source.y());

		if ((deltaX == 1 && deltaY == 2) || (deltaX == 2 && deltaY == 1)) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "knight move rule is violated";
	}
}