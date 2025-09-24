
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
        // only applies if the moved piece is actually a Knight
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place src = move.getSource();
        Place dst = move.getDestination();
        int dx = dst.getX() - src.getX();
        int dy = dst.getY() - src.getY();

        // knight moves only 2+1 in any direction
        if (!((Math.abs(dx) == 2 && Math.abs(dy) == 1) ||
              (Math.abs(dx) == 1 && Math.abs(dy) == 2))) {
            // not a knight‐style move → nothing to block here
            return true;
        }

        // compute the "leg" square that must be unoccupied
        Place leg;
        if (Math.abs(dx) == 2) {
            // horizontal 2, vertical 1: block at horizontal halfway
            leg = Place.of(src.getX() + dx / 2, src.getY());
        } else {
            // vertical 2, horizontal 1: block at vertical halfway
            leg = Place.of(src.getX(), src.getY() + dy / 2);
        }

        // if there is any piece on the leg square, move is blocked
        return game.getPiece(leg) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}