
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Position;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int maxRow = game.getNumRows() - 1;
        int maxCol = game.getNumColumns() - 1;

        // source coordinate
        Position from = move.getFromPosition();
        // destination coordinate
        Position to   = move.getToPosition();

        boolean sourceInBounds =
                from.getRow()    >= 0 && from.getRow()    <= maxRow &&
                from.getColumn() >= 0 && from.getColumn() <= maxCol;
        boolean destInBounds =
                to.getRow()      >= 0 && to.getRow()      <= maxRow &&
                to.getColumn()   >= 0 && to.getColumn()   <= maxCol;

        return sourceInBounds && destInBounds;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}