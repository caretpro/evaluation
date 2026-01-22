
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
        // If the move number exceeds the protected move count, rule does not restrict
        if (game.getNumMoves() >= this.numProtectedMoves) {
            return true;
        }

        // Get the source and destination pieces
        var sourcePiece = game.getPiece(move.getSource());
        var destinationPiece = game.getPiece(move.getDestination());

        // If there is no source piece, move is invalid (but validation of move legality is outside scope)
        if (sourcePiece == null) {
            return true; // Assuming move legality is validated elsewhere
        }

        // Check if the move is a capture: destination has opponent's piece
        if (destinationPiece != null && !destinationPiece.getPlayer().equals(sourcePiece.getPlayer())) {
            // Capture attempt within protected moves is not allowed
            return false;
        }

        // Otherwise, move is valid under this rule
        return true;
    }

    @Override
    public String getDescription() {
        return "Capturing piece in the first " + this.numProtectedMoves + " moves are not allowed";
    }
}