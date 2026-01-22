
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
        
        return isValidCoordinate(source.x(), size) && 
               isValidCoordinate(source.y(), size) && 
               isValidCoordinate(destination.x(), size) && 
               isValidCoordinate(destination.y(), size);
    }

    private boolean isValidCoordinate(int coordinate, int size) {
        return coordinate >= 0 && coordinate < size;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}