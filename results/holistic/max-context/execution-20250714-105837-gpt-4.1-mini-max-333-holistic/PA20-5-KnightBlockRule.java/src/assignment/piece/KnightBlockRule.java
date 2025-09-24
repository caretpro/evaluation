
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

        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();

        // Knight moves in L shape: (±2, ±1) or (±1, ±2)
        // Determine the blocking square ("leg") based on the move direction
        int blockX, blockY;

        if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
            // The blocking square is one step in the horizontal direction from source
            blockX = source.x() + dx / 2;
            blockY = source.y();
        } else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
            // The blocking square is one step in the vertical direction from source
            blockX = source.x();
            blockY = source.y() + dy / 2;
        } else {
            // Not a valid Knight move shape, so no blocking rule applies here
            return true;
        }

        // Check if the blocking square is occupied
        if (game.getPiece(blockX, blockY) != null) {
            return false;
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}