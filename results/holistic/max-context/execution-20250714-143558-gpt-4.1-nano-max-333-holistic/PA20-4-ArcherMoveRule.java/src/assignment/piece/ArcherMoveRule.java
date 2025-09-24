
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.piece.Piece;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Archer)) {
            return true; // Not an Archer piece, rule does not apply
        }
        
        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        // Check if move is along a straight line
        if (srcX != destX && srcY != destY) {
            return false; // Not a straight line
        }

        int countPiecesBetween = 0;

        // Determine direction and count pieces between source and destination
        if (srcX == destX) {
            // Vertical move
            int startY = Math.min(srcY, destY) + 1;
            int endY = Math.max(srcY, destY);
            for (int y = startY; y < endY; y++) {
                if (game.getPiece(srcX, y) != null) {
                    countPiecesBetween++;
                }
            }
        } else {
            // Horizontal move
            int startX = Math.min(srcX, destX) + 1;
            int endX = Math.max(srcX, destX);
            for (int x = startX; x < endX; x++) {
                if (game.getPiece(x, srcY) != null) {
                    countPiecesBetween++;
                }
            }
        }

        Piece destPiece = game.getPiece(move.getDestination());

        if (destPiece == null) {
            // Moving to empty square: valid only if exactly one piece in between
            return countPiecesBetween == 1;
        } else {
            // Capturing: valid only if exactly one piece in between and opponent's piece
            return countPiecesBetween == 1 && !destPiece.getPlayer().equals(game.getCurrentPlayer());
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}