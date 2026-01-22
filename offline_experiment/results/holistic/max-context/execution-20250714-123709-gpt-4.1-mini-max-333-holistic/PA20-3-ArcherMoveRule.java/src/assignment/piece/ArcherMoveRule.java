
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

        int srcX = src.x();
        int srcY = src.y();
        int dstX = dst.x();
        int dstY = dst.y();

        // Must move strictly horizontally or vertically
        if (srcX != dstX && srcY != dstY) {
            return false;
        }

        // Determine direction and step
        int stepX = Integer.compare(dstX, srcX);
        int stepY = Integer.compare(dstY, srcY);

        int countBetween = 0;

        // Count pieces between source and destination (excluding both ends)
        int x = srcX + stepX;
        int y = srcY + stepY;
        while (x != dstX || y != dstY) {
            if (game.getPiece(x, y) != null) {
                countBetween++;
            }
            x += stepX;
            y += stepY;
        }

        Piece destPiece = game.getPiece(dst);

        if (destPiece == null) {
            // Non-capturing move: path must be clear (no pieces in between)
            return countBetween == 0;
        } else {
            // Capturing move: must jump exactly one piece in between
            // Also, cannot capture own piece
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