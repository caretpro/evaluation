
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
    private int moveCount = 0;

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        // Increment move count and check if we're still in protected period
        moveCount++;
        if (moveCount <= numProtectedMoves) {
            // Assume any move that changes position is potentially a capture
            // (since we can't directly check the destination piece)
            return !move.isPositionChanged();
        }
        return true; // Valid move after protected period
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}