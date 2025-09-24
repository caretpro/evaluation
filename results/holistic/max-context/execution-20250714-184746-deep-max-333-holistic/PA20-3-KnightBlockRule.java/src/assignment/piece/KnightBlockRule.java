
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
        
        // Calculate the difference between source and destination
        int dx = destination.x() - source.x();
        int dy = destination.y() - source.y();
        
        // Check if it's a valid knight move (L-shape: 2 squares in one direction and 1 square perpendicular)
        if (!((Math.abs(dx) == 2 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && Math.abs(dy) == 2))) {
            return true; // Not a knight move, let other rules handle it
        }
        
        // Check for blocking piece (adjacent square in the direction of the longer move)
        Place blockingSquare;
        if (Math.abs(dx) > Math.abs(dy)) {
            // Moving 2 horizontally, check the square 1 step in that direction
            blockingSquare = new Place(source.x() + dx/2, source.y());
        } else {
            // Moving 2 vertically, check the square 1 step in that direction
            blockingSquare = new Place(source.x(), source.y() + dy/2);
        }
        
        // If there's a piece at the blocking square, the move is invalid
        return game.getPiece(blockingSquare) == null;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}