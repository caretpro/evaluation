
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
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Archer)) {
            return true;
        }

        Place src = move.getSource();
        Place dst = move.getDestination();

        // The source and destination must be different
        if (src.equals(dst)) {
            return false;
        }

        int srcX = src.x();
        int srcY = src.y();
        int dstX = dst.x();
        int dstY = dst.y();

        // Move must be strictly horizontal or vertical
        if (srcX != dstX && srcY != dstY) {
            return false;
        }

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

        Piece destPiece = game.getPiece(dst);

        if (destPiece == null) {
            // Destination empty: no pieces between allowed
            return countBetween == 0;
        } else {
            // Destination occupied: must be opponent piece and exactly one piece between
            if (destPiece.getPlayer().equals(piece.getPlayer())) {
                return false; // cannot capture own piece
            }
            return countBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}