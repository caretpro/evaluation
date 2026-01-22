
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place sourcePlace = move.getSource();
        Place destinationPlace = move.getDestination();

        Piece destinationPiece = game.getPieceAt(destinationPlace);
        if (destinationPiece == null) {
            // Destination is empty, move is valid
            return true;
        }

        Piece sourcePiece = game.getPieceAt(sourcePlace);
        if (sourcePiece == null) {
            // No piece at source, invalid move
            return false;
        }

        // Check if the piece at source and destination belong to the same player
        return !destinationPiece.getPlayer().equals(sourcePiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}