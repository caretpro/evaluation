
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

		int sourceRow = move.getSource().getX();
		int sourceCol = move.getSource().getY();
		int destRow = move.getDestination().getX();
		int destCol = move.getDestination().getY();

		int rowDiff = Math.abs(destRow - sourceRow);
		int colDiff = Math.abs(destCol - sourceCol);

		// Knight moves in an L shape: 2 by 1 or 1 by 2
		return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
	}

	@Override
	public String getDescription() {
		return "knight move rule is violated";
	}
}