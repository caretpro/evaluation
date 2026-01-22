
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
        int size = game.getConfiguration().getSize();
        Place source = move.getSource();
        Place destination = move.getDestination();

        boolean sourceInBounds = source.x() >= 0 && source.x() < size && source.y() >= 0 && source.y() < size;
        boolean destinationInBounds = destination.x() >= 0 && destination.x() < size && destination.y() >= 0 && destination.y() < size;

        return sourceInBounds && destinationInBounds;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}