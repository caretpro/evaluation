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
		int sourceX = move.getSource().getX();
		int sourceY = move.getSource().getY();
		int destX = move.getDestination().getX();
		int destY = move.getDestination().getY();
		int rowDiff = Math.abs(destX - sourceX);
		int colDiff = Math.abs(destY - sourceY);
		boolean isValidKnightMove = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
		assignment.protocol.Piece sourcePiece = game.getPiece(move.getSource());
		assignment.protocol.Piece destinationPiece = game.getPiece(move.getDestination());
		if (destinationPiece != null && destinationPiece.getColor() == sourcePiece.getColor()) {
			return false;
		}
		return isValidKnightMove;
	}
}
