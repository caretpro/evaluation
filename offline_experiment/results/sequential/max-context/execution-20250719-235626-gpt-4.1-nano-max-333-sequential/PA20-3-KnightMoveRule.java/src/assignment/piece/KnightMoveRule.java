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
		Piece sourcePiece = game.getPiece(move.getSource());
		if (sourcePiece == null) {
			return false;
		}
		int deltaX = Math.abs(move.getDestination().x() - move.getSource().x());
		int deltaY = Math.abs(move.getDestination().y() - move.getSource().y());
		boolean isValidKnightMove = (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2);
		if (!isValidKnightMove) {
			return false;
		}
		Piece destPiece = game.getPiece(move.getDestination());
		if (destPiece != null && destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
			return false;
		}
		return true;
	}
}
