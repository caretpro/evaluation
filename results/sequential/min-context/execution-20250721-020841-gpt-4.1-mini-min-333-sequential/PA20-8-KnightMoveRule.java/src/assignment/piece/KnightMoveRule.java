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
		int sourceIndex = move.getSource().toIndex();
		int destIndex = move.getDestination().toIndex();
		int sourceRow = sourceIndex / 8;
		int sourceCol = sourceIndex % 8;
		int destRow = destIndex / 8;
		int destCol = destIndex % 8;
		int rowDiff = Math.abs(destRow - sourceRow);
		int colDiff = Math.abs(destCol - sourceCol);
		return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
	}
}
