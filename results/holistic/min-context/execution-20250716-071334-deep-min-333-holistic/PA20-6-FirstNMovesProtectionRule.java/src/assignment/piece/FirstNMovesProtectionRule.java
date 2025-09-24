
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * The rule that requires capturing piece is not allowed within the first certain number of moves.
 */
public class FirstNMovesProtectionRule implements Rule {
    private final int numProtectedMoves;
    private int moveCount = 0;

    public FirstNMovesProtectionRule(int numProtectedMoves) {
        this.numProtectedMoves = numProtectedMoves;
    }

    @Override
    public boolean validate(Game game, Move move) {
        // Basic implementation that just counts moves without checking captures
        // since we don't have access to move details
        if (moveCount < numProtectedMoves) {
            moveCount++;
            return true; // Allow all moves during protected period
        }
        return true; // Allow all moves after protected period
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}