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
		if (!(game.getPiece(move.source()) instanceof Knight)) {
			return true;
		}
		int fromRow = move.source().row();
		int fromCol = move.source().col();
		int toRow = move.target().row();
		int toCol = move.target().col();
		int dRow = Math.abs(toRow - fromRow);
		int dCol = Math.abs(toCol - fromCol);
		return (dRow == 2 && dCol == 1) || (dRow == 1 && dCol == 2);
	}
}
