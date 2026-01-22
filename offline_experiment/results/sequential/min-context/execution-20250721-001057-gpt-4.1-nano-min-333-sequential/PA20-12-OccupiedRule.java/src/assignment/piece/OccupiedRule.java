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
		var destinationPiece = game.getPieceAt(move.getDestination());
		var movingPiece = game.getPieceAt(move.getSource());
		if (destinationPiece != null && movingPiece != null) {
			return !destinationPiece.getOwner().equals(movingPiece.getOwner());
		}
		return true;
	}
}
