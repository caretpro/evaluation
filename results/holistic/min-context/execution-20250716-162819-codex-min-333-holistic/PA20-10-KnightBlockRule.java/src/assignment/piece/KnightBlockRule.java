
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The blocking rule applying on Knights. The rule is similar to the blocking rule for horse in Chinese chess.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Xiangqi#Horse">Wikipedia</a>
 */
public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // Only apply to Knights
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place src = move.getSource();
        Place dst = move.getDestination();

        int dr = dst.rowIndex() - src.rowIndex();
        int dc = dst.colIndex() - src.colIndex();

        // Determine the "leg" (orthogonal step) for the knight‐style move
        Place leg;
        if (Math.abs(dr) == 2 && Math.abs(dc) == 1) {
            // Vertical‐first move: leg is one row from src towards dst
            leg = new Place(src.rowIndex() + Integer.signum(dr), src.colIndex());
        } else if (Math.abs(dr) == 1 && Math.abs(dc) == 2) {
            // Horizontal‐first move: leg is one column from src towards dst
            leg = new Place(src.rowIndex(), src.colIndex() + Integer.signum(dc));
        } else {
            // Not a knight move shape—no blocking applies here
            return true;
        }

        // If the "leg" square is occupied, the knight is blocked
        return game.getPiece(leg) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}