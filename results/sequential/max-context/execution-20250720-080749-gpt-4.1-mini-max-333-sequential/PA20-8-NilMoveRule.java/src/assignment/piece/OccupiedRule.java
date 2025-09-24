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
        var sourcePiece = game.getPiece(move.getSource());
        var destinationPiece = game.getPiece(move.getDestination());
        if (destinationPiece == null) {
            return true;
        }
        return sourcePiece == null || !destinationPiece.getPlayer().equals(sourcePiece.getPlayer());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}
