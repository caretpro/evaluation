
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int boardSize = game.getBoard().getSize();
        return move.getSource().getRow() >= 0 && move.getSource().getRow() < boardSize &&
               move.getSource().getCol() >= 0 && move.getSource().getCol() < boardSize &&
               move.getDestination().getRow() >= 0 && move.getDestination().getRow() < boardSize &&
               move.getDestination().getCol() >= 0 && move.getDestination().getCol() < boardSize;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}