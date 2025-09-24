
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
        Position to = move.getTo();
        Piece destinationPiece = game.getPiece(to);

        if (destinationPiece != null) {
            Position from = move.getFrom();
            Piece movingPiece = game.getPiece(from);

            if (movingPiece != null && destinationPiece.getOwner().equals(movingPiece.getOwner())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}