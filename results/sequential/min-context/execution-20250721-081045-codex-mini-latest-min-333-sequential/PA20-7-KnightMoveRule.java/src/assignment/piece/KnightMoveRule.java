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
		int fromRow = move.getSource().getRow();
		int fromCol = move.getSource().getCol();
		int toRow = move.getTarget().getRow();
		int toCol = move.getTarget().getCol();
		int dRow = Math.abs(toRow - fromRow);
		int dCol = Math.abs(toCol - fromCol);
		return (dRow == 2 && dCol == 1) || (dRow == 1 && dCol == 2);
	}
}
