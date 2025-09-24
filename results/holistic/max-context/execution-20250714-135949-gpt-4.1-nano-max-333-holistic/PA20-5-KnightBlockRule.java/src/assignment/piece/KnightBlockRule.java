
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import assignment.protocol.Piece;

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
        
        Place source = move.getSource();
        Place destination = move.getDestination();

        int deltaX = destination.x() - source.x();
        int deltaY = destination.y() - source.y();

        // Check for valid Knight move (L-shape)
        if (!((Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) || (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 2))) {
            return true; // Not a valid Knight move, rule does not apply
        }

        // Determine the position of the "horse-leg" (the square that must be unblocked)
        Place blockPlace;
        if (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) {
            // Horizontal move: check the square in the middle horizontally
            int blockX = source.x() + (deltaX / 2);
            int blockY = source.y();
            blockPlace = new Place(blockX, blockY);
        } else {
            // Vertical move: check the square in the middle vertically
            int blockX = source.x();
            int blockY = source.y() + (deltaY / 2);
            blockPlace = new Place(blockX, blockY);
        }

        // Check if the blocking position is occupied
        Piece blockingPiece = game.getPiece(blockPlace);
        if (blockingPiece != null) {
            return false; // Move is blocked
        }
        return true; // Move is not blocked
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}