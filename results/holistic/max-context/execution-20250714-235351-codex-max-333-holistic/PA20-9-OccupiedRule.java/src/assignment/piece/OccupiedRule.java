
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
        // get the moving piece
        Piece moving = game.getPiece(move.getSource());
        // get any piece at the destination
        Piece dest = game.getPiece(move.getDestination());
        // invalid if destination occupied by a piece of the same player
        return dest == null || !dest.getPlayer().equals(moving.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}