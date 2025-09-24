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
		if (!(game.getPiece(move.getFrom()) instanceof Knight)) {
			return true;
		}
		int sourceX = move.getFrom().getX();
		int sourceY = move.getFrom().getY();
		int targetX = move.getTo().getX();
		int targetY = move.getTo().getY();
		int xDiff = Math.abs(targetX - sourceX);
		int yDiff = Math.abs(targetY - sourceY);
		return (xDiff == 2 && yDiff == 1) || (xDiff == 1 && yDiff == 2);
	}
}
