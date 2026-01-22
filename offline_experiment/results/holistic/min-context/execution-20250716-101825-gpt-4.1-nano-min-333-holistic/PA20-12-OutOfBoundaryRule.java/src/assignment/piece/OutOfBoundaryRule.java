
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
import java.awt.Dimension;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // Retrieve board size
        Dimension size = game.getSize();

        // Get source and destination positions
        Place source = move.getSource();
        Place destination = move.getDestination();

        // Check if source is within boundaries
        boolean sourceInBounds = source.getRow() >= 0 && source.getRow() < size.height
                && source.getColumn() >= 0 && source.getColumn() < size.width;

        // Check if destination is within boundaries
        boolean destinationInBounds = destination.getRow() >= 0 && destination.getRow() < size.height
                && destination.getColumn() >= 0 && destination.getColumn() < size.width;

        // Valid if both source and destination are within boundaries
        return sourceInBounds && destinationInBounds;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}