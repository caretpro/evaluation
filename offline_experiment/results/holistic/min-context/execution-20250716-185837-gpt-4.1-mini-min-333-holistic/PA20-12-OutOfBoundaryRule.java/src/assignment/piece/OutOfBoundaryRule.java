
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        var board = game.getBoard();
        int rows = board.length;
        int cols = board[0].length;

        int srcRow = move.getSourceRow();
        int srcCol = move.getSourceCol();
        int destRow = move.getDestinationRow();
        int destCol = move.getDestinationCol();

        boolean sourceInBounds = srcRow >= 0 && srcRow < rows && srcCol >= 0 && srcCol < cols;
        boolean destinationInBounds = destRow >= 0 && destRow < rows && destCol >= 0 && destCol < cols;

        return sourceInBounds && destinationInBounds;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}