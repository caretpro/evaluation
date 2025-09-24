
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();
        Place src = move.getSource();
        Place dst = move.getDestination();

        return isWithinBounds(src.x(), size)
                && isWithinBounds(src.y(), size)
                && isWithinBounds(dst.x(), size)
                && isWithinBounds(dst.y(), size);
    }

    private boolean isWithinBounds(int coord, int size) {
        return coord >= 0 && coord < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}