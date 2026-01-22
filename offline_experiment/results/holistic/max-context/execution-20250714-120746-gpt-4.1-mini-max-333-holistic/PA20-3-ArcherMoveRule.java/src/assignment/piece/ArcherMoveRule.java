
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }

        Place src = move.getSource();
        Place dst = move.getDestination();

        int srcX = src.x();
        int srcY = src.y();
        int dstX = dst.x();
        int dstY = dst.y();

        // Must move in a straight line (horizontal or vertical)
        if (srcX != dstX && srcY != dstY) {
            return false;
        }

        Piece sourcePiece = game.getPiece(src);
        Piece destPiece = game.getPiece(dst);

        // Count pieces between source and destination
        int countBetween = 0;

        if (srcX == dstX) {
            // Vertical move
            int minY = Math.min(srcY, dstY);
            int maxY = Math.max(srcY, dstY);
            for (int y = minY + 1; y < maxY; y++) {
                if (game.getPiece(srcX, y) != null) {
                    countBetween++;
                }
            }
        } else {
            // Horizontal move
            int minX = Math.min(srcX, dstX);
            int maxX = Math.max(srcX, dstX);
            for (int x = minX + 1; x < maxX; x++) {
                if (game.getPiece(x, srcY) != null) {
                    countBetween++;
                }
            }
        }

        if (destPiece == null) {
            // Move without capture: no pieces in between allowed
            return countBetween == 0;
        } else {
            // Capture move: must have exactly one piece in between
            // Also cannot capture own piece
            if (destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
                return false;
            }
            return countBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}