
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (game == null || move == null) {
            return false;
        }
        if (move.getSource() == null || move.getDestination() == null) {
            return false;
        }

        int size = game.getConfiguration().getSize();
        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        // Check if source and destination are within [0, size-1]
        if (sourceX < 0 || sourceX >= size) return false;
        if (sourceY < 0 || sourceY >= size) return false;
        if (destX < 0 || destX >= size) return false;
        if (destY < 0 || destY >= size) return false;

        return true;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}