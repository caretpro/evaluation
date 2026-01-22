
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Position;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Position destination = move.getDestination();
        Piece destinationPiece = game.getPieceAt(destination);
        Piece movingPiece = game.getPieceAt(move.getSource());

        // If there's no piece at the destination, move is valid
        if (destinationPiece == null) {
            return true;
        }
        // If the piece at destination belongs to the same player, move is invalid
        return !destinationPiece.getPlayer().equals(movingPiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}