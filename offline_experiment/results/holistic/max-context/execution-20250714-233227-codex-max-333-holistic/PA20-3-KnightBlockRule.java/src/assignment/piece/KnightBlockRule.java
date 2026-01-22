
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The blocking rule applying on Knights. The rule is similar to the blocking rule for horse in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Horse'>Wikipedia</a>
 */
public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // only apply to Knight pieces; otherwise always OK
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place src = move.getSource();
        Place dst = move.getDestination();
        int dx = dst.x() - src.x();
        int dy = dst.y() - src.y();

        // must be an L‑shape: one dimension = ±2, the other = ±1
        if (!((Math.abs(dx) == 2 && Math.abs(dy) == 1)
           || (Math.abs(dx) == 1 && Math.abs(dy) == 2))) {
            return true;  // let other rules catch non‑knight moves
        }

        // identify the “leg” square that blocks the knight in Xiangqi:
        // if dx==±2 & dy==±1, block is one step horizontally from source;
        // if dx==±1 & dy==±2, block is one step vertically from source.
        Place leg;
        if (Math.abs(dx) == 2) {
            leg = new Place(src.x() + Integer.signum(dx), src.y());
        } else {
            leg = new Place(src.x(), src.y() + Integer.signum(dy));
        }

        // if there is any piece on the leg square, the knight is blocked
        return game.getPiece(leg) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}