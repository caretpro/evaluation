
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;

/**
 * Global rule that requires that a piece should not move to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece == null) {
            // If no piece at source, this rule does not apply; let other rules handle invalid moves
            return true;
        }
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece != null && destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
            // Destination occupied by same player's piece - invalid move
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "a piece cannot move to a destination occupied by another piece of the same player";
    }
}