
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
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Archer)) {
            // Not an Archer piece, rule does not apply
            return true;
        }

        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        // Check if move is in a straight line
        if (srcX != destX && srcY != destY) {
            return false; // Not a straight line
        }

        int countBetween = 0;

        if (srcX == destX) {
            // Moving vertically
            int startY = Math.min(srcY, destY) + 1;
            int endY = Math.max(srcY, destY);
            for (int y = startY; y < endY; y++) {
                if (game.getPiece(srcX, y) != null) {
                    countBetween++;
                }
            }
        } else {
            // Moving horizontally
            int startX = Math.min(srcX, destX) + 1;
            int endX = Math.max(srcX, destX);
            for (int x = startX; x < endX; x++) {
                if (game.getPiece(x, srcY) != null) {
                    countBetween++;
                }
            }
        }

        Piece destPiece = game.getPiece(move.getDestination());

        if (destPiece == null) {
            // Moving to an empty square: no pieces in between
            return countBetween == 0;
        } else {
            // Moving to capture: exactly one piece in between
            // and the destination piece should be of opponent
            return countBetween == 1 && !destPiece.getPlayer().equals(game.getCurrentPlayer());
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}