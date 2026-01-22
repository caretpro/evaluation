
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
        
        // Knight moves in L-shape (2 squares in one direction and 1 square perpendicular)
        if (Math.abs(dx) + Math.abs(dy) != 3 || Math.abs(dx) == 0 || Math.abs(dy) == 0) {
            return false;
        }
        
        // Determine the blocking position (adjacent square in the direction of the 2-square move)
        Place blockingPosition;
        if (Math.abs(dx) == 2) {
            blockingPosition = new Place(source.x() + dx/2, source.y());
        } else {
            blockingPosition = new Place(source.x(), source.y() + dy/2);
        }
        
        // Check if there's a piece at the blocking position
        return game.getPiece(blockingPosition) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}