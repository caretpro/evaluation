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
		int srcRow = move.getSource().rowIndex();
		int srcCol = move.getSource().columnIndex();
		int dstRow = move.getDestination().rowIndex();
		int dstCol = move.getDestination().columnIndex();
		int dRow = Math.abs(dstRow - srcRow);
		int dCol = Math.abs(dstCol - srcCol);
		return (dRow == 2 && dCol == 1) || (dRow == 1 && dCol == 2);
	}
}
