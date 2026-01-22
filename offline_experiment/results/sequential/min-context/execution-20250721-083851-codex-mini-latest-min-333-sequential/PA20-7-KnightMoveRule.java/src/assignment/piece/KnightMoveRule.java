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
		int dx = Math.abs(move.getDestination().getCol() - move.getSource().getCol());
		int dy = Math.abs(move.getDestination().getRow() - move.getSource().getRow());
		return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
	}
}
