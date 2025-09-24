
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.piece.Piece;

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
        // If the number of moves made so far is less than the protected move count
        if (game.getNumMoves() < this.numProtectedMoves) {
            Piece destinationPiece = game.getPiece(move.getDestination());
            // Check if the destination contains an opponent's piece
            if (destinationPiece != null && !destinationPiece.getPlayer().equals(game.getCurrentPlayer())) {
                // Capture move detected within protected moves, invalid
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