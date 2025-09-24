
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
            return true; // Not a knight, rule does not apply
        }

        Place source = move.getSource();
        Place target = move.getTarget();

        int deltaX = target.getX() - source.getX();
        int deltaY = target.getY() - source.getY();

        // Check for valid knight move (L-shape)
        if (!((Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) || (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 2))) {
            return true; // Not a valid knight move, so rule does not block
        }

        // Determine the blocking position based on move direction
        Place blockingPosition;
        if (Math.abs(deltaX) == 2) {
            // Horizontal move, blocking is one step horizontally
            int blockX = source.getX() + (deltaX / 2);
            int blockY = source.getY();
            blockingPosition = new Place(blockX, blockY);
        } else {
            // Vertical move, blocking is one step vertically
            int blockX = source.getX();
            int blockY = source.getY() + (deltaY / 2);
            blockingPosition = new Place(blockX, blockY);
        }

        // Check if the blocking position is occupied
        if (game.getPiece(blockingPosition) != null) {
            return false; // Move is blocked
        }

        return true; // Move is valid if not blocked
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}