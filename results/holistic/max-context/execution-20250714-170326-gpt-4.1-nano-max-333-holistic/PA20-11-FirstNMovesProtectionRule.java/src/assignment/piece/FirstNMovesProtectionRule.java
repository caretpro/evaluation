
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Player;

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
            // Get the source and destination pieces
            Piece sourcePiece = game.getPiece(move.getSource());
            Piece destinationPiece = game.getPiece(move.getDestination());

            // If there's no source piece, move is invalid (but rule validation assumes move is valid)
            if (sourcePiece == null) {
                return true; // No piece to move, rule doesn't apply
            }

            // If destination is occupied by an opponent's piece, it's a capture
            if (destinationPiece != null && !destinationPiece.getPlayer().equals(sourcePiece.getPlayer())) {
                // Capture is not allowed within the protected moves
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