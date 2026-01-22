
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
        // If the number of moves made so far is less than the protected moves threshold,
        // capturing is not allowed.
        if (game.getNumMoves() < numProtectedMoves) {
            Piece destinationPiece = game.getPiece(move.getDestination());
            if (destinationPiece != null) {
                // There is a piece at the destination, so this is a capturing move.
                // Disallow capturing in the first numProtectedMoves moves.
                return false;
            }
        }
        // Otherwise, the move is valid under this rule.
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}