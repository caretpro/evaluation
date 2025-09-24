
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
        Piece sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece == null) {
            // No piece at source, invalid move
            return false;
        }
        Piece destinationPiece = game.getPiece(move.getDestination());
        if (destinationPiece != null && destinationPiece.getPlayer() == sourcePiece.getPlayer()) {
            // Destination occupied by a piece of the same player
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}