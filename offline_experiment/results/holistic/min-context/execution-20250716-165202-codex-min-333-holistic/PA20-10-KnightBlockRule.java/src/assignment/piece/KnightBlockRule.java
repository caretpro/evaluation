
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
        // Only applies to Knights; otherwise always valid
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place from = move.getSource();
        Place to   = move.getDestination();

        int dx = to.getColumn() - from.getColumn();
        int dy = to.getRow()    - from.getRow();

        int adx = Math.abs(dx);
        int ady = Math.abs(dy);
        // If it's not a knight‑move, let other rules catch it
        if (!((adx == 2 && ady == 1) || (adx == 1 && ady == 2))) {
            return true;
        }

        // Compute the “leg” square that can block the knight:
        int legCol = from.getColumn() + (adx == 2 ? dx / 2 : 0);
        int legRow = from.getRow()    + (ady == 2 ? dy / 2 : 0);
        Place leg  = new Place(legRow, legCol);

        // Knight is blocked if any piece sits on that leg square
        return game.getPiece(leg) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}