
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.game.Piece;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // Get the piece at the destination
        Piece destinationPiece = game.getPieceAt(move.getDestination());
        
        // If there's no piece at destination, move is valid
        if (destinationPiece == null) {
            return true;
        }
        
        // Get the moving piece
        Piece movingPiece = game.getPieceAt(move.getSource());
        
        // Check if pieces belong to different players
        return movingPiece.getPlayer() != destinationPiece.getPlayer();
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}