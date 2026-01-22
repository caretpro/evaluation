
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
        Piece moving = game.getPiece(move.getSource());
        Piece dest    = game.getPiece(move.getDestination());
        if (dest == null) {
            // empty square is fine
            return true;
        }
        // cannot move onto a square occupied by your own piece
        return !dest.getPlayer().equals(moving.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}