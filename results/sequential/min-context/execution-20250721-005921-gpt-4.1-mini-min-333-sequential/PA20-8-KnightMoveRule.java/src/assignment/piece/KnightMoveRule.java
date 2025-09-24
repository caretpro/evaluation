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
		int srcRow = move.getSource().getRowIndex();
		int srcCol = move.getSource().getColumnIndex();
		int destRow = move.getDestination().getRowIndex();
		int destCol = move.getDestination().getColumnIndex();
		int rowDiff = Math.abs(destRow - srcRow);
		int colDiff = Math.abs(destCol - srcCol);
		return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
	}
}
