
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
        // Check if destination square is occupied
        if (game.getPieceAt(move.getToPosition()) != null) {
            // Check if the occupying piece belongs to the same player
            return game.getPieceAt(move.getToPosition()).getPlayer() != move.getMovingPiece().getPlayer();
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}