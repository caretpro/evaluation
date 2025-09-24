
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
        int boardSize = game.getConfiguration().getSize();
        Place source = move.getSource();
        Place destination = move.getDestination();
        
        // Check if source is within bounds
        boolean sourceValid = source.x() >= 0 && source.x() < boardSize && 
                             source.y() >= 0 && source.y() < boardSize;
        
        // Check if destination is within bounds
        boolean destinationValid = destination.x() >= 0 && destination.x() < boardSize && 
                                  destination.y() >= 0 && destination.y() < boardSize;
        
        return sourceValid && destinationValid;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}