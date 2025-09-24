
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
        if (game.getNumMoves() >= this.numProtectedMoves) {
            return true;
        }

        // Check if the move is a capture
        if (game.getPieceAt(move.getEndRow(), move.getEndCol()) != null) {
            return false; // Capturing is not allowed
        }

        return true; // Not a capture, so the move is valid
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}