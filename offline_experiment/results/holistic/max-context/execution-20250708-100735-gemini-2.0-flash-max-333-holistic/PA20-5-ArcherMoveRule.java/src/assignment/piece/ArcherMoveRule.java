
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
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

        if (source.equals(destination)) {
            return false;
        }

        if (source.x() != destination.x() && source.y() != destination.y()) {
            return false; // Only horizontal or vertical movement allowed
        }

        int obstacles = 0;
        if (source.x() == destination.x()) { // Vertical movement
            int startY = Math.min(source.y(), destination.y());
            int endY = Math.max(source.y(), destination.y());
            for (int y = startY + 1; y < endY; y++) {
                if (game.getPiece(source.x(), y) != null) {
                    obstacles++;
                }
            }
        } else { // Horizontal movement
            int startX = Math.min(source.x(), destination.x());
            int endX = Math.max(source.x(), destination.x());
            for (int x = startX + 1; x < endX; x++) {
                if (game.getPiece(x, source.y()) != null) {
                    obstacles++;
                }
            }
        }

        Piece destinationPiece = game.getPiece(destination);
        if (destinationPiece == null) {
            // If destination is empty, there should be no obstacles
            return obstacles == 0;
        } else {
            // If destination is not empty, there should be exactly one obstacle
            return obstacles == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}