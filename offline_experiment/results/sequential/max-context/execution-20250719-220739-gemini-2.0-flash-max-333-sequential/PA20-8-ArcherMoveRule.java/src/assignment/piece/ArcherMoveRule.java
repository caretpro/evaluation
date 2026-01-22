
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
            int count = 0;
            int start = Math.min(sourceY, destY);
            int end = Math.max(sourceY, destY);
            for (int i = start + 1; i < end; i++) {
                if (game.getPiece(sourceX, i) != null) {
                    count++;
                }
            }
            Piece destinationPiece = game.getPiece(move.getDestination());
            if (count == 1) {
                return destinationPiece == null || destinationPiece.getPlayer() != game.getPiece(move.getSource()).getPlayer();
            }
        } else if (sourceY == destY) {
            int count = 0;
            int start = Math.min(sourceX, destX);
            int end = Math.max(sourceX, destX);
            for (int i = start + 1; i < end; i++) {
                if (game.getPiece(i, sourceY) != null) {
                    count++;
                }
            }
            Piece destinationPiece = game.getPiece(move.getDestination());
            if (count == 1) {
                return destinationPiece == null || destinationPiece.getPlayer() != game.getPiece(move.getSource()).getPlayer();
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}