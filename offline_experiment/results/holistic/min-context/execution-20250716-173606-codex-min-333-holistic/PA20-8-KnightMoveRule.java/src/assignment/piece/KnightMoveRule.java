
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // If the piece at source is not a Knight, this rule does not apply:
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        // Compute row and column differences between source and destination:
        Place src = move.getSource();
        Place dst = move.getDestination();
        int rowDelta = Math.abs(src.getRow() - dst.getRow());
        int colDelta = Math.abs(src.getColumn() - dst.getColumn());

        // Valid knight move: (2,1) or (1,2)
        return (rowDelta == 2 && colDelta == 1)
            || (rowDelta == 1 && colDelta == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}