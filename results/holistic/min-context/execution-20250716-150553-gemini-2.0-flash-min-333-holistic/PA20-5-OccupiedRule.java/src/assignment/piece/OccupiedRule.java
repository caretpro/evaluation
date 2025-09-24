
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
		Piece piece = game.getPiece(move.getSource());
		if (piece == null) {
			return false;
		}
		Piece destinationPiece = game.getPiece(move.getTarget());

		if (destinationPiece != null) {
			Player movingPlayer = piece.getPlayer();
			Player destinationPlayer = destinationPiece.getPlayer();

			if (movingPlayer != null && destinationPlayer != null && movingPlayer.equals(destinationPlayer)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String getDescription() {
		return "piece cannot be captured by another piece belonging to the same player";
	}
}