
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Player;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // Get the destination place
        var destination = move.getDestination();
        // Get the piece at the destination
        Piece destPiece = game.getPiece(destination);
        // Get the piece being moved
        Piece movingPiece = game.getPiece(move.getSource());

        // If there's no piece at destination, move is valid
        if (destPiece == null) {
            return true;
        }

        // If the destination piece belongs to the same player, move is invalid
        Player destPlayer = destPiece.getPlayer();
        Player movingPlayer = movingPiece.getPlayer();

        // Move is invalid if both pieces belong to the same player
        return !destPlayer.equals(movingPlayer);
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}