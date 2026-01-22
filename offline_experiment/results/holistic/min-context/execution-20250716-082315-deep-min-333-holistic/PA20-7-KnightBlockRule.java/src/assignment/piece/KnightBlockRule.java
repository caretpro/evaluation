
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
        Place destination = move.getDestination();
        
        // Calculate the move direction
        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();
        
        // Knight moves in L-shape (2 squares in one direction and 1 square perpendicular)
        if (Math.abs(dx) + Math.abs(dy) != 3 || Math.abs(dx) == 0 || Math.abs(dy) == 0) {
            return false; // Not a valid knight move
        }
        
        // Check for blocking piece (Chinese chess horse rule)
        if (Math.abs(dx) == 2) {
            // Moving horizontally first, check for piece one square in x direction
            int blockingX = source.x() + (dx > 0 ? 1 : -1);
            if (game.getPiece(new Place(blockingX, source.y())) != null) {
                return false; // Move is blocked
            }
        } else { // Math.abs(dy) == 2
            // Moving vertically first, check for piece one square in y direction
            int blockingY = source.y() + (dy > 0 ? 1 : -1);
            if (game.getPiece(new Place(source.x(), blockingY)) != null) {
                return false; // Move is blocked
            }
        }
        
        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}