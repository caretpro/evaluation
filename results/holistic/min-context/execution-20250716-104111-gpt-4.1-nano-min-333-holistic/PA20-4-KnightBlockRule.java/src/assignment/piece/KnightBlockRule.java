
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
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place source = move.getSource();
        Place target = move.getDestination();

        int deltaX = target.getX() - source.getX();
        int deltaY = target.getY() - source.getY();

        // Determine the direction of the move
        if (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) {
            // Horizontal move
            int legX = source.getX() + (deltaX / 2);
            int legY = source.getY();
            if (game.getPiece(new Place(legX, legY)) != null) {
                return false; // blocked
            }
        } else if (Math.abs(deltaY) == 2 && Math.abs(deltaX) == 1) {
            // Vertical move
            int legX = source.getX();
            int legY = source.getY() + (deltaY / 2);
            if (game.getPiece(new Place(legX, legY)) != null) {
                return false; // blocked
            }
        } else {
            // Not a valid knight move
            return false;
        }
        return true; // move is not blocked
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}