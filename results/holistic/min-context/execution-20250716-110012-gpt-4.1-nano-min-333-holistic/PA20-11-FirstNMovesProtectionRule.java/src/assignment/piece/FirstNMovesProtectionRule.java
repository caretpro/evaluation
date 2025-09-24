
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
        // Check if the current move number is within the protected range
        int currentMoveNumber = game.getMoveNumber(); // Correct method assumed
        // If move number is less than or equal to protected moves, capturing is not allowed
        if (currentMoveNumber <= this.numProtectedMoves) {
            // If the move is a capture, validation fails
            if (move.isCapture()) { // Correct method assumed
                return false;
            }
        }
        // Otherwise, move is valid
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}