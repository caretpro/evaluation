
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * The rule that requires capturing piece is not allowed within the first certain number of moves.
 */
public class FirstNMovesProtectionRule implements Rule {
    /**
     * The number of moves within which capturing piece is not allowed.
     */
    private final int numProtectedMoves;

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        // Check if current move count is within the protected initial moves
        if (game.getNumMoves() < this.numProtectedMoves) {
            // Get the piece at source and destination
            var sourcePiece = game.getPiece(move.getSource());
            var destPiece = game.getPiece(move.getDestination());

            // If there is a piece at destination and it belongs to a different player, it's a capture
            if (destPiece != null && sourcePiece != null) {
                if (!sourcePiece.getPlayer().equals(destPiece.getPlayer())) {
                    // Capture move within protected moves is not allowed
                    return false;
                }
            }
        }
        // Move is valid if not a capture during protected moves
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}