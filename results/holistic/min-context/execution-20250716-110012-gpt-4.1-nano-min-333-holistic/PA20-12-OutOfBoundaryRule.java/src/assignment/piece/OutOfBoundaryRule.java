
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getSize(); // Correct method to get board size
        int sourceRow = move.getSource().getRow();
        int sourceCol = move.getSource().getCol();
        int destRow = move.getDestination().getRow();
        int destCol = move.getDestination().getCol();

        boolean sourceInBounds = sourceRow >= 0 && sourceRow < size && sourceCol >= 0 && sourceCol < size;
        boolean destInBounds = destRow >= 0 && destRow < size && destCol >= 0 && destCol < size;

        return sourceInBounds && destInBounds;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}