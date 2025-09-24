
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination of a move should not be the same.
 */
public class NilMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (move == null || move.getSource() == null || move.getDestination() == null) {
            return false;
        }
        return !move.getSource().equals(move.getDestination());
    }

    @Override
    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}