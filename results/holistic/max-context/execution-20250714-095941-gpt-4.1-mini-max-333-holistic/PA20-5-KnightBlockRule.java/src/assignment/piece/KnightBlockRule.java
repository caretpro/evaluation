
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

        Place src = move.getSource();
        Place dst = move.getDestination();

        int dx = dst.x() - src.x();
        int dy = dst.y() - src.y();

        // Knight moves in L shape: (±2, ±1) or (±1, ±2)
        if (!((Math.abs(dx) == 2 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && Math.abs(dy) == 2))) {
            // Not a knight move, so no blocking rule applies here
            return true;
        }

        // Determine the "horse leg" position that must be empty
        Place blockPos;
        if (Math.abs(dx) == 2) {
            // The blocking square is one step horizontally from source in the direction of dx
            blockPos = new Place(src.x() + dx / 2, src.y());
        } else {
            // The blocking square is one step vertically from source in the direction of dy
            blockPos = new Place(src.x(), src.y() + dy / 2);
        }

        // Check board boundaries
        int size = game.getConfiguration().getSize();
        if (blockPos.x() < 0 || blockPos.x() >= size || blockPos.y() < 0 || blockPos.y() >= size) {
            // Blocking position outside board, no blocking piece possible
            return true;
        }

        // If the blocking square is occupied, the knight is blocked
        if (game.getPiece(blockPos) != null) {
            return false;
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}