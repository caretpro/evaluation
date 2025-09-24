
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
        // Only apply to Knight moves; other pieces are unaffected by this rule
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place src = move.getSource();
        Place dst = move.getDestination();

        int dx = dst.x() - src.x();
        int dy = dst.y() - src.y();

        int adx = Math.abs(dx);
        int ady = Math.abs(dy);

        // Only L‑shaped knight moves can be blocked (1×2 or 2×1)
        if ((adx == 1 && ady == 2)) {
            // vertical-first move: check the square immediately above/below source
            int stepY = dy / ady;  // ±1
            Place blockPoint = new Place(src.x(), src.y() + stepY);
            return game.getPiece(blockPoint) == null;
        } else if ((adx == 2 && ady == 1)) {
            // horizontal-first move: check the square immediately left/right of source
            int stepX = dx / adx;  // ±1
            Place blockPoint = new Place(src.x() + stepX, src.y());
            return game.getPiece(blockPoint) == null;
        }

        // Non‑knight‑move patterns aren’t this rule’s concern
        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}