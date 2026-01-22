
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
        // Check if current move count is within the protected moves
        if (game.getNumMoves() < this.numProtectedMoves) {
            // Get the piece at source and destination
            Piece sourcePiece = game.getPiece(move.getSource());
            Piece destPiece = game.getPiece(move.getDestination());

            // If source piece is null, move is invalid, but rule validation only concerns captures
            if (sourcePiece == null) {
                return true; // Or false, depending on overall move validation logic
            }

            // If destination has opponent's piece, it's a capture
            if (destPiece != null && !destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
                // Capture is not allowed in the first protected moves
                return false;
            }
        }
        // Either outside protected moves or move is not a capture
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}