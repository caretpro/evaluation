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
		int x1 = move.getSource().x();
		int y1 = move.getSource().y();
		int x2 = move.getDestination().x();
		int y2 = move.getDestination().y();
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
	}
}
