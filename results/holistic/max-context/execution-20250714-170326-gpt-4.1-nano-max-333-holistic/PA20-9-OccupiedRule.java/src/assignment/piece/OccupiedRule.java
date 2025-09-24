
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
        // Get the piece at the destination
        var destinationPiece = game.getPiece(move.getDestination());
        // Get the moving piece
        var movingPiece = game.getPiece(move.getSource());

        // If there's no piece at the destination, move is valid
        if (destinationPiece == null) {
            return true;
        }

        // If the destination is occupied by a piece belonging to the same player, move is invalid
        if (destinationPiece.getPlayer().equals(movingPiece.getPlayer())) {
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