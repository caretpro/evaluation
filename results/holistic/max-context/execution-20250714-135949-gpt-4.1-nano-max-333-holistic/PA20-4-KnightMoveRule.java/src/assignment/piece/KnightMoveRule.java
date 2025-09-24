
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // Check if the move involves a Knight piece
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true; // Not a Knight move, rule does not apply
        }

        // Enforce protection rules during initial moves
        int moveCount = game.getNumMoves();
        int protectionThreshold = game.getConfiguration().getNumMovesProtection();

        // If within protected moves phase
        if (moveCount < protectionThreshold) {
            // Check if the move captures a piece
            Piece targetPiece = game.getPiece(move.getDestination());
            if (targetPiece != null) {
                // Capture is not allowed during protection phase
                return false;
            }
            // Check if move results in a win (e.g., moving to central place)
            // Assuming game.getCentralPlace() is the winning condition
            Place dest = move.getDestination();
            if (dest.equals(game.getCentralPlace())) {
                // Moving to central place is not allowed during protection
                return false;
            }
        }

        // Validate the knight's move pattern (L-shape)
        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        int dx = Math.abs(destX - sourceX);
        int dy = Math.abs(destY - sourceY);

        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}