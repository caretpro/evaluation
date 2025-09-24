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
		int sourceX = move.getSource().x();
		int sourceY = move.getSource().y();
		int destX = move.getDestination().x();
		int destY = move.getDestination().y();
		int deltaX = Math.abs(destX - sourceX);
		int deltaY = Math.abs(destY - sourceY);
		if ((deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2)) {
			return true;
		}
		return false;
	}
}
