
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        Place destination = move.getDestination();
        int boardSize = game.getConfiguration().getSize();

        return source.x() >= 0 && source.x() < boardSize &&
                source.y() >= 0 && source.y() < boardSize &&
                destination.x() >= 0 && destination.x() < boardSize &&
                destination.y() >= 0 && destination.y() < boardSize;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}