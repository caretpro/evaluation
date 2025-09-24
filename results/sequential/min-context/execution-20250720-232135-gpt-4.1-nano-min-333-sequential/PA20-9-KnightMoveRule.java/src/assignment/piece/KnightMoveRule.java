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
		Place source = (Place) move.getSource();
		Place target = (Place) move.getTarget();
		int sourceRow = source.getRow();
		int sourceCol = source.getCol();
		int targetRow = target.getRow();
		int targetCol = target.getCol();
		int rowDiff = Math.abs(targetRow - sourceRow);
		int colDiff = Math.abs(targetCol - sourceCol);
		return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
	}
}
