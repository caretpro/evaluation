
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination of a move should not be the same.
 */
public class NilMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // The move is valid only if its start- and end-positions are different
        return !move.start().equals(move.end());
    }

    @Override
    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}