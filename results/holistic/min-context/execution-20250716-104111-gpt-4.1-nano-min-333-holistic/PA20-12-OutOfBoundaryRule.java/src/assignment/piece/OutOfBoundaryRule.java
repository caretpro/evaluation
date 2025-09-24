
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int rows = game.getRows();
        int cols = game.getCols();

        // Check source position
        int sourceRow = move.getSource().getRow();
        int sourceCol = move.getSource().getCol();

        // Check destination position
        int destRow = move.getDestination().getRow();
        int destCol = move.getDestination().getCol();

        boolean sourceInBounds = sourceRow >= 0 && sourceRow < rows && sourceCol >= 0 && sourceCol < cols;
        boolean destInBounds = destRow >= 0 && destRow < rows && destCol >= 0 && destCol < cols;

        return sourceInBounds && destInBounds;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}