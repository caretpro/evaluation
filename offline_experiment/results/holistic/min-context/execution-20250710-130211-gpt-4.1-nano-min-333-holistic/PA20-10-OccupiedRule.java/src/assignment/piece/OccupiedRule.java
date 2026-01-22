
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place; // Corrected import based on move.getDestination() return type

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // Get the destination position from the move
        Place destination = move.getDestination(); // Using Place instead of Position
        // Get the piece at the destination position
        Piece destinationPiece = game.getPieceAt(destination);
        // If there is a piece and it belongs to the same player, move is invalid
        if (destinationPiece != null && destinationPiece.getPlayer().equals(game.getCurrentPlayer())) {
            return false;
        }
        // Otherwise, move is valid
        return true;
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}