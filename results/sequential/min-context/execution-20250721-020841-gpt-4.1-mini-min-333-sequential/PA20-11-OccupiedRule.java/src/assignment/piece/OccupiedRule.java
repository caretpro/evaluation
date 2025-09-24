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
		var destination = move.to;
		var origin = move.from;
		var movingPiece = game.getPieceAt(origin);
		var pieceAtDestination = game.getPieceAt(destination);
		if (pieceAtDestination == null) {
			return true;
		}
		return !pieceAtDestination.getPlayer().equals(movingPiece.getPlayer());
	}
}
