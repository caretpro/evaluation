
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;

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
        // Only disallow capturing moves within the first numProtectedMoves moves.
        if (game.getNumMoves() < numProtectedMoves) {
            Piece destinationPiece = game.getPiece(move.getDestination());
            if (destinationPiece != null) {
                // Capturing is not allowed in the protected moves period.
                return false;
            }
        }
        // Otherwise, allow the move.
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}