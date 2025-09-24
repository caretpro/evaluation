
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
        // Check if there's a piece at the destination
        Piece destinationPiece = game.getPiece(move.getDestination());
        if (destinationPiece == null) {
            return true; // No piece at destination - move is valid
        }
        
        // Get the moving piece
        Piece movingPiece = game.getPiece(move.getOrigin());
        if (movingPiece == null) {
            return false; // No piece at origin - invalid move
        }
        
        // Check if pieces belong to the same player
        return !movingPiece.getPlayer().equals(destinationPiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}