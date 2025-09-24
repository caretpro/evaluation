
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

        int xDiff = Math.abs(source.x() - destination.x());
        int yDiff = Math.abs(source.y() - destination.y());

        if ((xDiff == 0 && yDiff == 0) || (xDiff != 0 && yDiff != 0)) {
            return false;
        }

        int count = 0;
        if (xDiff == 0) {
            int start = Math.min(source.y(), destination.y()) + 1;
            int end = Math.max(source.y(), destination.y());
            for (int i = start; i < end; i++) {
                if (game.getPiece(source.x(), i) != null) {
                    count++;
                }
            }

        } else {
            int start = Math.min(source.x(), destination.x()) + 1;
            int end = Math.max(source.x(), destination.x());
            for (int i = start; i < end; i++) {
                if (game.getPiece(i, source.y()) != null) {
                    count++;
                }
            }
        }

        Piece destPiece = game.getPiece(destination);
        if (destPiece == null) {
            if (count != 0) {
                return false;
            }
        } else {
            if (count != 1) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}