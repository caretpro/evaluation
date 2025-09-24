
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int[] boardSize = game.getBoardSize(); // [width, height]
        int boardWidth = boardSize[0];
        int boardHeight = boardSize[1];

        int[] source = move.getSource();       // [column, row]
        int[] destination = move.getDestination();

        int srcX = source[0];
        int srcY = source[1];
        int destX = destination[0];
        int destY = destination[1];

        boolean sourceInside = srcX >= 0 && srcX < boardWidth && srcY >= 0 && srcY < boardHeight;
        boolean destinationInside = destX >= 0 && destX < boardWidth && destY >= 0 && destY < boardHeight;

        return sourceInside && destinationInside;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}