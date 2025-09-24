
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // Get the piece at the destination position
        var destinationPiece = game.getPiece(move.getDestination());
        // If there's no piece at the destination, move is valid
        if (destinationPiece == null) {
            return true;
        }
        // Check if the piece at destination belongs to the current player
        return !destinationPiece.getPlayer().equals(game.getCurrentPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}