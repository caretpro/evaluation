
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;

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
        int sourceX = move.getSource().x();
        int sourceY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        if (sourceX == destX) {
            int obstacles = 0;
            int start = Math.min(sourceY, destY);
            int end = Math.max(sourceY, destY);
            for (int y = start + 1; y < end; y++) {
                if (game.getPiece(sourceX, y) != null) {
                    obstacles++;
                }
            }
            Piece destinationPiece = game.getPiece(destX, destY);
            if (destinationPiece == null && obstacles == 0) {
                return true;
            } else if (destinationPiece != null && obstacles == 1) {
                return true;
            }
        } else if (sourceY == destY) {
            int obstacles = 0;
            int start = Math.min(sourceX, destX);
            int end = Math.max(sourceX, destX);
            for (int x = start + 1; x < end; x++) {
                if (game.getPiece(x, sourceY) != null) {
                    obstacles++;
                }
            }
            Piece destinationPiece = game.getPiece(destX, destY);
            if (destinationPiece == null && obstacles == 0) {
                return true;
            } else if (destinationPiece != null && obstacles == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}