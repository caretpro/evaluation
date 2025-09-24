
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
        // Retrieve the piece at the destination position
        var destinationPiece = game.getPieceAtPosition(move.getDestination());
        // If there's no piece at the destination, move is valid
        if (destinationPiece == null) {
            return true;
        }
        // Check if the piece at the destination belongs to the same player
        return !destinationPiece.getPlayer().equals(move.getPiece().getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}