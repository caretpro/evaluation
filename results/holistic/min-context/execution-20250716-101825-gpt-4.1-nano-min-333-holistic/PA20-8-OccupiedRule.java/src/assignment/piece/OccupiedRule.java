
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
        // Retrieve the destination place
        var destinationPlace = move.getDestination();
        // Retrieve the piece at the destination place
        Piece destinationPiece = game.getPieceAt(destinationPlace);
        // If there's no piece at the destination, move is valid
        if (destinationPiece == null) {
            return true;
        }
        // Retrieve the moving piece
        Piece movingPiece = move.getPiece();
        // Check if the piece at the destination belongs to the same player
        return !destinationPiece.getPlayer().equals(movingPiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}