
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
        // If we are still within the protected move count and this move would capture a piece...
        if (game.getMoveCount() < numProtectedMoves && move.getCapturedPiece() != null) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}