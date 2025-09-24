
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Player;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		Piece sourcePiece = game.getPiece(move.getSource());
		Piece destinationPiece = game.getPiece(move.getDestination());

		if (sourcePiece == null) {
			return false; // Source must have a piece
		}

		if (destinationPiece != null) {
			Player sourcePlayer = sourcePiece.getPlayer();
			Player destinationPlayer = destinationPiece.getPlayer();
			return !sourcePlayer.equals(destinationPlayer); // Destination must not have a piece of the same owner
		}

		return true; // Destination is free
	}

	@Override
	public String getDescription() {
		return "piece cannot be captured by another piece belonging to the same player";
	}
}