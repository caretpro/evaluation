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
		int rowDiff = Math.abs(move.getDestination().getX() - move.getSource().getX());
		int colDiff = Math.abs(move.getDestination().getY() - move.getSource().getY());
		boolean isLShape = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
		if (!isLShape) {
			return false;
		}
		Piece sourcePiece = game.getPiece(move.getSource());
		Piece destinationPiece = game.getPiece(move.getDestination());
		if (destinationPiece != null && destinationPiece.getColor() == sourcePiece.getColor()) {
			return false;
		}
		return true;
	}
}
