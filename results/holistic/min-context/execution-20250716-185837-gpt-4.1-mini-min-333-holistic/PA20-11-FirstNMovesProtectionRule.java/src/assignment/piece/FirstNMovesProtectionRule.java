
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
        int moveCount = 0;
        try {
            moveCount = game.getMoves().size();
        } catch (Exception e) {
            // fallback if getMoves() is not available
            moveCount = 0;
        }
        boolean isCapture = false;
        try {
            isCapture = move.getCapturedPiece() != null;
        } catch (Exception e) {
            // fallback if getCapturedPiece() is not available
            isCapture = false;
        }
        if (moveCount < numProtectedMoves && isCapture) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}