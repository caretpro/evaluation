
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // only apply when the moving piece is an Archer
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }

        Place src = move.getSource();
        Place dst = move.getDestination();

        // must move in a straight line (no diagonal)
        if (src.x() != dst.x() && src.y() != dst.y()) {
            return false;
        }

        int dx = Integer.compare(dst.x(), src.x());
        int dy = Integer.compare(dst.y(), src.y());

        // count how many pieces are strictly between source and destination
        int betweenCount = 0;
        for (int x = src.x() + dx, y = src.y() + dy;
             x != dst.x() || y != dst.y();
             x += dx, y += dy) {
            if (game.getPiece(new Place(x, y)) != null) {
                betweenCount++;
            }
        }

        Piece target = game.getPiece(dst);
        if (target == null) {
            // non‑capture move: path must be clear (no pieces in between)
            return betweenCount == 0;
        } else {
            // capture move: must jump exactly one piece in between
            return betweenCount == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}