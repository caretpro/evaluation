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
		int sourceRow = move.getSource().getRow();
		int sourceCol = move.getSource().getCol();
		int targetRow = move.getTarget().getRow();
		int targetCol = move.getTarget().getCol();
		int rowDiff = Math.abs(targetRow - sourceRow);
		int colDiff = Math.abs(targetCol - sourceCol);
		if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
			return true;
		}
		return false;
	}
}
