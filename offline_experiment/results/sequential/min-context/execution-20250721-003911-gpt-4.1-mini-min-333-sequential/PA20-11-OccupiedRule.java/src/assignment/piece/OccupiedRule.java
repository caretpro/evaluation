package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
	@Override
	public String getDescription() {
		return "piece cannot be captured by another piece belonging to the same player";
	}

	@Override
	public boolean validate(Game game, Move move) {
		var destination = move.destination;
		var pieceAtDestination = game.getPiece(destination);
		if (pieceAtDestination == null) {
			return true;
		}
		var movingPiece = game.getPiece(move.origin);
		if (movingPiece == null) {
			return false;
		}
		return !movingPiece.getPlayer().equals(pieceAtDestination.getPlayer());
	}
}
