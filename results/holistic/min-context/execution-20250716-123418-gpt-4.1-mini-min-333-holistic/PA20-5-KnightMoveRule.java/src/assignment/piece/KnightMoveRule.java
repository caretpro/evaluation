
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

	@Override
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Knight)) {
			return true;
		}
		int sourceRow = move.getSource().row();
		int sourceCol = move.getSource().column();
		int destRow = move.getDestination().row();
		int destCol = move.getDestination().column();

		int dRow = Math.abs(destRow - sourceRow);
		int dCol = Math.abs(destCol - sourceCol);

		// Knight moves in an L shape: (2,1) or (1,2)
		return (dRow == 2 && dCol == 1) || (dRow == 1 && dCol == 2);
	}

	@Override
	public String getDescription() {
		return "knight move rule is violated";
	}
}