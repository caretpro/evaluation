
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
        // only apply if the moving piece is actually a Knight
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        // compute delta
        int dx = move.getDestination().x() - move.getSource().x();
        int dy = move.getDestination().y() - move.getSource().y();
        // knight moves are (±1,±2) or (±2,±1)
        int adx = Math.abs(dx), ady = Math.abs(dy);
        if (!((adx == 1 && ady == 2) || (adx == 2 && ady == 1))) {
            // not a knight‐move pattern; let other rules report it
            return true;
        }
        // find the “leg” that must be free: if moving 2 in x, check the square one step in x; if 2 in y, check one step in y
        int legX = move.getSource().x() + Integer.signum(dx) * (adx == 2 ? 1 : 0);
        int legY = move.getSource().y() + Integer.signum(dy) * (ady == 2 ? 1 : 0);
        Place leg = new Place(legX, legY);
        // invalid if something blocks the leg
        return game.getPiece(leg) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}