
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
        
        // Calculate the difference in coordinates
        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();
        
        // Check if it's a valid knight move (L-shape)
        if (!((Math.abs(dx) == 2 && Math.abs(dy) == 1) || 
              (Math.abs(dx) == 1 && Math.abs(dy) == 2))) {
            return true; // Not a knight move, let other rules handle it
        }
        
        // Check for blocking piece
        if (Math.abs(dx) > Math.abs(dy)) {
            // Horizontal move first (2 steps), check middle position
            int blockX = source.x() + (dx > 0 ? 1 : -1);
            if (game.getPiece(blockX, source.y()) != null) {
                return false; // Blocked
            }
        } else {
            // Vertical move first (2 steps), check middle position
            int blockY = source.y() + (dy > 0 ? 1 : -1);
            if (game.getPiece(source.x(), blockY) != null) {
                return false; // Blocked
            }
        }
        
        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}