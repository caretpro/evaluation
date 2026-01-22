
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
            return true; // Not a Knight, rule does not apply
        }

        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        int deltaX = destX - srcX;
        int deltaY = destY - srcY;

        // Check for valid Knight move pattern: (2,1), (1,2), (-1,2), (-2,1), etc.
        boolean validPattern = (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) ||
                               (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 2);
        if (!validPattern) {
            return false; // Invalid move pattern for a Knight
        }

        // Determine the blocking position based on move pattern
        if (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) {
            // Horizontal move, blocking is at (srcX + deltaX/2, srcY)
            int blockX = srcX + deltaX / 2;
            int blockY = srcY;
            if (game.getPiece(new Place(blockX, blockY)) != null) {
                return false; // Blocked
            }
        } else if (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 2) {
            // Vertical move, blocking is at (srcX, srcY + deltaY/2)
            int blockX = srcX;
            int blockY = srcY + deltaY / 2;
            if (game.getPiece(new Place(blockX, blockY)) != null) {
                return false; // Blocked
            }
        }

        return true; // Move is valid and not blocked
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}