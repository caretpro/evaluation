
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int boardSize = game.boardSize();
        return move.from().x() >= 0 && move.from().x() < boardSize &&
               move.from().y() >= 0 && move.from().y() < boardSize &&
               move.to().x() >= 0 && move.to().x() < boardSize &&
               move.to().y() >= 0 && move.to().y() < boardSize;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}