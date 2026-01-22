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
		String source = move.getSource().toString();
		String dest = move.getDestination().toString();
		int colDiff = Math.abs(dest.charAt(0) - source.charAt(0));
		int rowDiff = Math.abs(dest.charAt(1) - source.charAt(1));
		return (colDiff == 2 && rowDiff == 1) || (colDiff == 1 && rowDiff == 2);
	}
}
