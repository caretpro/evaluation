
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int boardWidth = game.boardWidth;
        int boardHeight = game.boardHeight;

        int sourceX = move.sourceX;
        int sourceY = move.sourceY;
        int destX = move.destX;
        int destY = move.destY;

        boolean sourceInBounds = sourceX >= 0 && sourceX < boardWidth && sourceY >= 0 && sourceY < boardHeight;
        boolean destInBounds = destX >= 0 && destX < boardWidth && destY >= 0 && destY < boardHeight;

        return sourceInBounds && destInBounds;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}