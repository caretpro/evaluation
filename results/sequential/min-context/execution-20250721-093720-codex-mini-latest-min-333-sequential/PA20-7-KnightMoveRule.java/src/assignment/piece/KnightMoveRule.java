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
		int fromFile = move.getSource().getFile();
		int fromRank = move.getSource().getRank();
		int toFile = move.getDestination().getFile();
		int toRank = move.getDestination().getRank();
		int df = Math.abs(toFile - fromFile);
		int dr = Math.abs(toRank - fromRank);
		return (df == 2 && dr == 1) || (df == 1 && dr == 2);
	}
}
