
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int dstX = move.getDestination().x();
        int dstY = move.getDestination().y();

        // both source and destination must be within [0, size)
        if (srcX < 0 || srcX >= size
         || srcY < 0 || srcY >= size
         || dstX < 0 || dstX >= size
         || dstY < 0 || dstY >= size) {
            return false;
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}