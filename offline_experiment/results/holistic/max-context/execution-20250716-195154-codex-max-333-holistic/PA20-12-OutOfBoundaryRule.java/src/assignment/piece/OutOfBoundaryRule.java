
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Configuration;
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
        return inBounds(src, size) && inBounds(dst, size);
    }

    private boolean inBounds(Place p, int size) {
        int x = p.x();
        int y = p.y();
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}