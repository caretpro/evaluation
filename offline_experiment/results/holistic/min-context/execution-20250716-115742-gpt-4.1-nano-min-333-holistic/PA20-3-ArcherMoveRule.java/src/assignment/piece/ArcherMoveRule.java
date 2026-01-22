
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }

        Place source = move.getSource();
        Place destination = move.getDestination();

        int srcX = source.getX();
        int srcY = source.getY();
        int destX = destination.getX();
        int destY = destination.getY();

        // Check if move is in a straight line
        if (srcX != destX && srcY != destY) {
            return false; // Not a straight line
        }

        int countBetween = 0;

        // Determine direction and count pieces between source and destination
        if (srcX == destX) {
            // Horizontal move
            int startY = Math.min(srcY, destY) + 1;
            int endY = Math.max(srcY, destY);
            for (int y = startY; y < endY; y++) {
                Place checkPlace = new Place(srcX, y);
                if (game.getPiece(checkPlace) != null) {
                    countBetween++;
                }
            }
        } else {
            // Vertical move
            int startX = Math.min(srcX, destX) + 1;
            int endX = Math.max(srcX, destX);
            for (int x = startX; x < endX; x++) {
                Place checkPlace = new Place(x, srcY);
                if (game.getPiece(checkPlace) != null) {
                    countBetween++;
                }
            }
        }

        // Check destination piece
        boolean destinationOccupied = game.getPiece(destination) != null;
        if (destinationOccupied) {
            // Capture move: must jump over exactly one piece
            return countBetween == 1;
        } else {
            // Normal move: no pieces in between
            return countBetween == 0;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}