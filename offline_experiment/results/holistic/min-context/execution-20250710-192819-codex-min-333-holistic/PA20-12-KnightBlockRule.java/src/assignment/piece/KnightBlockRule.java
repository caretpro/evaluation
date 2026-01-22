
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
        // only apply to knights
        if (!(game.pieceAt(move.source()) instanceof Knight)) {
            return true;
        }

        Place src = move.source();
        Place dst = move.target();

        int dx = dst.colIndex() - src.colIndex();
        int dy = dst.rowIndex() - src.rowIndex();

        // must be a knight‐style L‑move
        Place leg;
        if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
            // horizontal L: leg is one step horizontally
            leg = new Place(src.rowIndex(), src.colIndex() + dx / 2);
        } else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
            // vertical L: leg is one step vertically
            leg = new Place(src.rowIndex() + dy / 2, src.colIndex());
        } else {
            // not a knight move at all
            return true;
        }

        // blocked if there's any piece on the leg square
        return game.pieceAt(leg) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}