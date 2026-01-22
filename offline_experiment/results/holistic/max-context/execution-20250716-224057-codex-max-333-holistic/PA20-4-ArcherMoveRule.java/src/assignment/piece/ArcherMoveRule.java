
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The rule of moving of cannon in Chinese chess.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Xiangqi#Cannon">Wikipedia</a>
 */
public class CannonMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Cannon)) {
            return true; // not a cannon → this rule does not apply
        }

        Place src = move.getSource(), dst = move.getDestination();
        int dx = Math.abs(src.x() - dst.x()), dy = Math.abs(src.y() - dst.y());

        // must move strictly horizontally or vertically, and must not land on own piece
        if ((dx != 0 && dy != 0) ||
            (game.getPiece(dst) != null &&
             game.getPiece(dst).getPlayer() == game.getPiece(src).getPlayer())) {
            return false;
        }

        // count the number of pieces between src and dst
        int count = 0;
        int stepX = Integer.compare(dst.x(), src.x());
        int stepY = Integer.compare(dst.y(), src.y());
        int x = src.x() + stepX, y = src.y() + stepY;
        while (x != dst.x() || y != dst.y()) {
            if (game.getPiece(x, y) != null) {
                count++;
            }
            x += stepX;
            y += stepY;
        }

        // if landing on empty square → path must be clear (count == 0)
        // if capturing (landing on enemy) → must have exactly one screen (count == 1)
        if (game.getPiece(dst) == null) {
            return count == 0;
        } else {
            return count == 1;
        }
    }

    @Override
    public String getDescription() {
        return "cannon move rule is violated";
    }
}