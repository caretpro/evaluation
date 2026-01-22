
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getSize();
        int r0 = move.getFromRow(), c0 = move.getFromColumn();
        int r1 = move.getToRow(),   c1 = move.getToColumn();
        return r0 >= 0 && r0 < size
            && c0 >= 0 && c0 < size
            && r1 >= 0 && r1 < size
            && c1 >= 0 && c1 < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}