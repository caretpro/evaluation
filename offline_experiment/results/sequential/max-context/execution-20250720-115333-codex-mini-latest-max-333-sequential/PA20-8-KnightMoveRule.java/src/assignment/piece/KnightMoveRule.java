package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

	@Override
	public String getDescription() {
		return "knight move rule is violated";
	}

	@Override
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Knight)) {
			return true;
		}
		int dx = Math.abs(move.getSource().x() - move.getDestination().x());
		int dy = Math.abs(move.getSource().y() - move.getDestination().y());
		return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
	}
}
