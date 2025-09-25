package assignment.piece;

import assignment.piece.Rule;
import assignment.protocol.Configuration;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // TODO
        return false;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}
