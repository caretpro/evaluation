
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
        int dx = Math.abs(destination.x() - source.x());
        int dy = Math.abs(destination.y() - source.y());
        
        // Check if it's a valid knight move
        if (!((dx == 2 && dy == 1) || (dx == 1 && dy == 2))) {
            return true;
        }
        
        // Check for blocking piece
        if (dx == 2) {
            int blockingX = source.x() + (destination.x() > source.x() ? 1 : -1);
            if (game.getPiece(blockingX, source.y()) != null) {
                return false;
            }
        } else {
            int blockingY = source.y() + (destination.y() > source.y() ? 1 : -1);
            if (game.getPiece(source.x(), blockingY) != null) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}