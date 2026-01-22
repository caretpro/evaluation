
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

        // Determine the intermediate position (the "leg") that must be free
        Place legPosition;

        if (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) {
            // Moving horizontally then vertically
            int legX = source.getX() + (deltaX / 2);
            int legY = source.getY();
            legPosition = new Place(legX, legY);
        } else if (Math.abs(deltaY) == 2 && Math.abs(deltaX) == 1) {
            // Moving vertically then horizontally
            int legX = source.getX();
            int legY = source.getY() + (deltaY / 2);
            legPosition = new Place(legX, legY);
        } else {
            // Not a valid Knight move
            return false;
        }

        // Check if the leg position is occupied
        if (game.getPiece(legPosition) != null) {
            return false; // Move is blocked
        }

        return true; // Move is valid if not blocked
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}