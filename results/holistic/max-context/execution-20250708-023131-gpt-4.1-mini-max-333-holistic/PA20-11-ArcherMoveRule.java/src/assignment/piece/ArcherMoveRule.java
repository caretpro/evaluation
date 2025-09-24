
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

        // Must move strictly horizontally or vertically
        if (src.x() != dst.x() && src.y() != dst.y()) {
            return false;
        }

        int countBetween = 0;

        if (src.x() == dst.x()) {
            // vertical move
            int x = src.x();
            int startY = Math.min(src.y(), dst.y()) + 1;
            int endY = Math.max(src.y(), dst.y());
            for (int y = startY; y < endY; y++) {
                if (game.getPiece(x, y) != null) {
                    countBetween++;
                }
            }
        } else {
            // horizontal move
            int y = src.y();
            int startX = Math.min(src.x(), dst.x()) + 1;
            int endX = Math.max(src.x(), dst.x());
            for (int x = startX; x < endX; x++) {
                if (game.getPiece(x, y) != null) {
                    countBetween++;
                }
            }
        }

        Piece destPiece = game.getPiece(dst);
        if (destPiece == null) {
            // destination empty: no pieces between allowed
            return countBetween == 0;
        } else {
            // destination occupied: must be opponent piece and exactly one piece between
            if (destPiece.getPlayer().equals(piece.getPlayer())) {
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