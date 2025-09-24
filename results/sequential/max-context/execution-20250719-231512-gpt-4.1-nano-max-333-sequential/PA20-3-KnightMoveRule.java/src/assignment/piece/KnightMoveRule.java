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
		int dx = Math.abs(move.getDestination().x() - move.getSource().x());
		int dy = Math.abs(move.getDestination().y() - move.getSource().y());
		boolean isValidKnightMove = (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
		if (!isValidKnightMove) {
			return false;
		}
		Piece destinationPiece = game.getPiece(move.getDestination());
		Piece sourcePiece = game.getPiece(move.getSource());
		if (destinationPiece != null && destinationPiece.getPlayer().equals(sourcePiece.getPlayer())) {
			return false;
		}
		return true;
	}
}
