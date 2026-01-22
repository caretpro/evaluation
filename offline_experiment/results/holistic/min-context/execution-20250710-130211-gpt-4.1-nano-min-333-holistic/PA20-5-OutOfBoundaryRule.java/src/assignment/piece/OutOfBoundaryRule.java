
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
        // Get the board object from game
        var board = game.getBoard();

        // Get board dimensions
        int width = board.getWidth();
        int height = board.getHeight();

        // Get source and destination positions
        Place source = move.getSource();
        Place destination = move.getDestination();

        // Check if source is within boundary
        boolean sourceInBoundary = source.getColumn() >= 0 && source.getColumn() < width
                && source.getRow() >= 0 && source.getRow() < height;

        // Check if destination is within boundary
        boolean destinationInBoundary = destination.getColumn() >= 0 && destination.getColumn() < width
                && destination.getRow() >= 0 && destination.getRow() < height;

        // Valid if both source and destination are inside the boundary
        return sourceInBoundary && destinationInBoundary;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}