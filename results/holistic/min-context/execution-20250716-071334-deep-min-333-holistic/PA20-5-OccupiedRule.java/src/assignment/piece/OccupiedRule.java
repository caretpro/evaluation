
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
        if (game.getPieceAt(move.getDestination()) == null) {
            return true; // Destination is empty, move is valid
        }
        
        // Check if the piece at destination belongs to same player
        return !game.getPieceAt(move.getSource()).getPlayer().equals(
               game.getPieceAt(move.getDestination()).getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}