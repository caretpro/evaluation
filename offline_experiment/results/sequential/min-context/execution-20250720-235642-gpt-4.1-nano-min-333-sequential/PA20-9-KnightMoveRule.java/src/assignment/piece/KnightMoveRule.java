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
		int sourceX = move.getSource().getX();
		int sourceY = move.getSource().getY();
		int targetX = move.getDestination().getX();
		int targetY = move.getDestination().getY();
		int deltaX = Math.abs(targetX - sourceX);
		int deltaY = Math.abs(targetY - sourceY);
		return (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2);
	}
}
