
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		Piece destinationPiece = game.getPiece(move.getDestination());
		if (destinationPiece == null) {
			return true; // destination is empty, move is valid
		}
		// Check if the piece at destination belongs to the same player as the moving piece
		Piece movingPiece = game.getPiece(move.getSource());
		return movingPiece == null || !destinationPiece.getPlayer().equals(movingPiece.getPlayer());
	}

	@Override
	public String getDescription() {
		return "piece cannot be captured by another piece belonging to the same player";
	}
}