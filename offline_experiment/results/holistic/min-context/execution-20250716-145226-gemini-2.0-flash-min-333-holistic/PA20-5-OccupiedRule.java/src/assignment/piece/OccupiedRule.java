
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.player.Player;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		Piece piece = game.getPiece(move.from());
		if (piece == null) {
			return true;
		}
		Piece destinationPiece = game.getPiece(move.to());

		if (destinationPiece != null) {
			Player movingPiecePlayer = game.getPlayer(piece);
			Player destinationPiecePlayer = game.getPlayer(destinationPiece);

			if (movingPiecePlayer != null && destinationPiecePlayer != null && movingPiecePlayer.equals(destinationPiecePlayer)) {
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