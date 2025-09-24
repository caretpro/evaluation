
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // Get board dimensions
        int width = game.getBoardWidth();
        int height = game.getBoardHeight();

        // Get source and destination coordinates
        var source = move.getSource();
        var destination = move.getDestination();

        // Check if source is within boundaries
        boolean sourceInBounds = source.getX() >= 0 && source.getX() < width
                && source.getY() >= 0 && source.getY() < height;

        // Check if destination is within boundaries
        boolean destinationInBounds = destination.getX() >= 0 && destination.getX() < width
                && destination.getY() >= 0 && destination.getY() < height;

        // Return true if both source and destination are within boundaries
        return sourceInBounds && destinationInBounds;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}