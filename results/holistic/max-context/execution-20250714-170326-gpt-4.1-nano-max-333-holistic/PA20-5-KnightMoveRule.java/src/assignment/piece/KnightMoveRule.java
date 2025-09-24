
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // Check if the piece to move is a Knight
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true; // Not a Knight, rule does not apply
        }

        int deltaX = Math.abs(move.getDestination().x() - move.getSource().x());
        int deltaY = Math.abs(move.getDestination().y() - move.getSource().y());

        // Knight moves in an "L" shape: 2 in one direction and 1 in the other
        boolean isValidKnightMove = (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2);
        if (!isValidKnightMove) {
            return false; // Not a valid Knight move
        }

        // Additional protection rule checks
        int currentMoveCount = game.getNumMoves();
        int protectionMoves = game.getConfiguration().getNumMovesProtection();

        // If within initial protected moves, no captures or winning moves allowed
        if (currentMoveCount < protectionMoves) {
            Piece sourcePiece = game.getPiece(move.getSource());
            Piece targetPiece = game.getPiece(move.getDestination());

            // Moving to an occupied square (capture) is not allowed during protection moves
            if (targetPiece != null) {
                return false;
            }

            // Check if move results in a win (e.g., capturing central place or other game-specific condition)
            // For simplicity, assuming capturing central place or similar is a winning move
            Place central = game.getCentralPlace();
            if (move.getDestination().equals(central)) {
                return false; // Moving into central place during protection moves is not allowed
            }
        }

        // Check if the destination is occupied by a friendly piece
        Piece destPiece = game.getPiece(move.getDestination());
        if (destPiece != null && destPiece.getPlayer().equals(game.getCurrentPlayer())) {
            return false; // Cannot capture own piece
        }

        // Check if move is blocked by other rules (e.g., path blocked), but for Knight, no blocking
        // so no additional checks needed here

        return true; // All checks passed
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}