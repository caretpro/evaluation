package assignment.piece;

import assignment.piece.Rule;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * Global rule that requires the source and destination of a move should not be the same.
 */
public class NilMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // TODO
        return false;
    }

    @Override
    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}
