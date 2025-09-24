
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

        // Determine the move pattern
        if (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) {
            // Horizontal move, check the "leg" at (source.x + deltaX/2, source.y)
            int legX = source.getX() + deltaX / 2;
            int legY = source.getY();
            Place legPlace = new Place(legX, legY);
            if (game.getPiece(legPlace) != null) {
                return false; // blocked
            }
        } else if (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 2) {
            // Vertical move, check the "leg" at (source.x, source.y + deltaY/2)
            int legX = source.getX();
            int legY = source.getY() + deltaY / 2;
            Place legPlace = new Place(legX, legY);
            if (game.getPiece(legPlace) != null) {
                return false; // blocked
            }
        } else {
            // Not a valid knight move pattern, consider invalid or blocked
            return false;
        }

        // If no blocking piece found, move is valid
        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}