
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int width = game.getBoardWidth();
        int height = game.getBoardHeight();

        int srcRow = move.getSourceRow();
        int srcCol = move.getSourceCol();
        int destRow = move.getDestinationRow();
        int destCol = move.getDestinationCol();

        boolean sourceInBounds = srcRow >= 0 && srcRow < height && srcCol >= 0 && srcCol < width;
        boolean destinationInBounds = destRow >= 0 && destRow < height && destCol >= 0 && destCol < width;

        return sourceInBounds && destinationInBounds;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}