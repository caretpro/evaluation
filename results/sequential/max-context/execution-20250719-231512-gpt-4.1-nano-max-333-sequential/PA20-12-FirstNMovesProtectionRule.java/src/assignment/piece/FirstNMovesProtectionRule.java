package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * The rule that requires capturing piece is not allowed within the first certain number of moves.
 */
public class FirstNMovesProtectionRule implements Rule {
	/**
	 * The number of moves within which capturing piece is not allowed.
	 */
	private final int numProtectedMoves;

	public FirstNMovesProtectionRule(int numProtectedMoves) {
		this.numProtectedMoves = numProtectedMoves;
	}

	@Override
	public String getDescription() {
		return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
	}

	@Override
	public boolean validate(Game game, Move move) {
		if (game.getNumMoves() >= this.numProtectedMoves) {
			return true;
		}
		assignment.piece.Piece sourcePiece = game.getPiece(move.getSource());
		if (sourcePiece == null) {
			return true;
		}
		assignment.piece.Piece destinationPiece = game.getPiece(move.getDestination());
		if (destinationPiece != null && !destinationPiece.getPlayer().equals(sourcePiece.getPlayer())) {
			return false;
		}
		return true;
	}
}
