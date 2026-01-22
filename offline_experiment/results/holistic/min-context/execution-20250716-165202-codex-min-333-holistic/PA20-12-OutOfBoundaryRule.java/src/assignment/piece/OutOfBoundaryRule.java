
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        int w = game.getSizeX();
        int h = game.getSizeY();

        int sx = move.getSrcX();
        int sy = move.getSrcY();
        int dx = move.getDestX();
        int dy = move.getDestY();

        // both source and destination coordinates must be within [0..width-1] x [0..height-1]
        return sx >= 0 && sx < w
            && sy >= 0 && sy < h
            && dx >= 0 && dx < w
            && dy >= 0 && dy < h;
    }

    @Override
    public String getDescription() {
        return "place is out of boundary of gameboard";
    }
}