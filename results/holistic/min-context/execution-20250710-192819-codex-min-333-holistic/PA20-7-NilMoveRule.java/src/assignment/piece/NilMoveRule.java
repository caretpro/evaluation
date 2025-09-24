
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination of a move should not be the same.
 */
public class NilMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // source and destination must differ in at least one coordinate
        return move.getXFrom() != move.getXTo()
            || move.getYFrom() != move.getYTo();
    }

    @Override
    public String getDescription() {
        return "the source and destination of move should be different places";
    }
}