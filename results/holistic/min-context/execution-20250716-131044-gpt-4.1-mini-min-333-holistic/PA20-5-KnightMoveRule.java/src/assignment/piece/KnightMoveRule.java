
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
		int sourceRank = move.getSource().rank();
		int sourceFile = move.getSource().file();
		int destRank = move.getDestination().rank();
		int destFile = move.getDestination().file();

		int rankDiff = Math.abs(destRank - sourceRank);
		int fileDiff = Math.abs(destFile - sourceFile);

		// Knight moves in an L shape: 2 by 1 or 1 by 2
		return (rankDiff == 2 && fileDiff == 1) || (rankDiff == 1 && fileDiff == 2);
	}

	@Override
	public String getDescription() {
		return "knight move rule is violated";
	}
}