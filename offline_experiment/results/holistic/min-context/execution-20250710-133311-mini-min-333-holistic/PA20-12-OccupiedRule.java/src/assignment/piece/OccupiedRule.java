
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
		Piece movingPiece = game.getPieceOn(move.getSource());
		Piece destinationPiece = game.getPieceOn(move.getDestination());

		if (movingPiece == null) {
			// No piece to move, invalid move
			return false;
		}

		if (destinationPiece == null) {
			// Destination is empty, valid move
			return true;
		}

		// Check if destination piece belongs to the same player
		return !movingPiece.getPlayer().equals(destinationPiece.getPlayer());
	}

	@Override
	public String getDescription() {
		return "piece cannot be captured by another piece belonging to the same player";
	}
}