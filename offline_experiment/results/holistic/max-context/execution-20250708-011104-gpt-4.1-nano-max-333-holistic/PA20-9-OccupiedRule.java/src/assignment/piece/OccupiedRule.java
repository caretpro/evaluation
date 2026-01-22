
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Player;
import assignment.protocol.Place;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place destination = move.getDestination();
        Piece destinationPiece = game.getPiece(destination);
        if (destinationPiece == null) {
            // Destination is empty, move is valid
            return true;
        }
        Piece sourcePiece = game.getPiece(move.getSource());
        if (sourcePiece == null) {
            // Source piece is null, cannot validate properly; assume invalid move
            return false;
        }
        Player sourcePlayer = sourcePiece.getPlayer();
        Player destinationPlayer = destinationPiece.getPlayer();
        // Move is invalid if destination has a piece belonging to the same player
        return !sourcePlayer.equals(destinationPlayer);
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}