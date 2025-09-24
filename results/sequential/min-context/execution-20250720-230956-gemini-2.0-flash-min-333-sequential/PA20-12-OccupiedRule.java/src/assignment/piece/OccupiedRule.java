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
		assignment.protocol.Piece destinationPiece = game.getPiece(move.getTo().getRow(), move.getTo().getColumn());
		if (destinationPiece != null) {
			assignment.protocol.Player movingPiecePlayer = game
					.getPiece(move.getFrom().getRow(), move.getFrom().getColumn()).getPlayer();
			assignment.protocol.Player destinationPiecePlayer = destinationPiece.getPlayer();
			if (movingPiecePlayer.equals(destinationPiecePlayer)) {
				return false;
			}
		}
		return true;
	}
}
