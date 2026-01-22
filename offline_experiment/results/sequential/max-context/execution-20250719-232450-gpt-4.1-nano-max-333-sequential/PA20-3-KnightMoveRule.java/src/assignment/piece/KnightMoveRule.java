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
		assert assignment.piece.Piece;
		Piece piece = game.getPiece(move.getSource());
		if (!(piece instanceof Knight)) {
			return true;
		}
		int srcX = move.getSource().x();
		int srcY = move.getSource().y();
		int destX = move.getDestination().x();
		int destY = move.getDestination().y();
		int deltaX = Math.abs(destX - srcX);
		int deltaY = Math.abs(destY - srcY);
		return (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2);
	}
}
