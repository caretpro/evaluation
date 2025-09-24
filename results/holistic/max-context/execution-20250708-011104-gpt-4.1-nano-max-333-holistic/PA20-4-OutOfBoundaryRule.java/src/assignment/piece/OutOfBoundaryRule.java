
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int size = game.getConfiguration().getSize();

        // Get source and destination places
        var source = move.getSource();
        var destination = move.getDestination();

        // Check if source is within boundary
        boolean sourceInBoundary = source.x() >= 0 && source.x() < size
                && source.y() >= 0 && source.y() < size;

        // Check if destination is within boundary
        boolean destinationInBoundary = destination.x() >= 0 && destination.x() < size
                && destination.y() >= 0 && destination.y() < size;

        // Valid if both source and destination are inside boundary
        return sourceInBoundary && destinationInBoundary;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}