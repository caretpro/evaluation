
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int boardSize = game.size();
        return move.from().row() >= 0 && move.from().row() < boardSize &&
               move.from().column() >= 0 && move.from().column() < boardSize &&
               move.to().row() >= 0 && move.to().row() < boardSize &&
               move.to().column() >= 0 && move.to().column() < boardSize;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}