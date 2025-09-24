
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
        // Check if the move is a capture
        if (move.isCaptureMove()) {
            // Check if the current move number is within the protected range
            int moveNumber = game.getMoveNumber();
            if (moveNumber <= this.numProtectedMoves) {
                return false; // Move is invalid as it captures within protected moves
            }
        }
        return true; // Move is valid if not a capture or outside protected range
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}