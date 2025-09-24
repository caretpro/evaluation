
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int boardSize = game.getSize();
        return move.getFrom().getX() >= 0 && move.getFrom().getX() < boardSize &&
               move.getFrom().getY() >= 0 && move.getFrom().getY() < boardSize &&
               move.getTo().getX() >= 0 && move.getTo().getX() < boardSize &&
               move.getTo().getY() >= 0 && move.getTo().getY() < boardSize;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}