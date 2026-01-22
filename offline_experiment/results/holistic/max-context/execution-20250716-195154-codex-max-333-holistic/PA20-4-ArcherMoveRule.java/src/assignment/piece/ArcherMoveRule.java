
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;

import java.util.Objects;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // only apply this rule if the moving piece is actually an Archer
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        Place src = move.getSource();
        Place dst = move.getDestination();

        int dx = Integer.compare(dst.x(), src.x());
        int dy = Integer.compare(dst.y(), src.y());

        // must move in a straight line (rook‑style)
        if (dx != 0 && dy != 0) {
            return false;
        }

        // count intervening pieces strictly between src and dst
        int x = src.x() + dx, y = src.y() + dy;
        int screens = 0;
        while (x != dst.x() || y != dst.y()) {
            if (game.getPiece(new Place(x, y)) != null) {
                screens++;
            }
            x += dx;
            y += dy;
        }

        Piece target = game.getPiece(dst);
        if (target == null) {
            // non‑capturing move: no screens allowed
            return screens == 0;
        } else {
            // capturing move: exactly one screen required
            return screens == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}