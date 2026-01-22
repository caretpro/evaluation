
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Player;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // Check if the piece at source is an Archer
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            // Not an Archer, rule does not apply
            return true;
        }

        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        // Check if move is in a straight line
        if (srcX != destX && srcY != destY) {
            return false; // Not straight line
        }

        int countPiecesBetween = 0;

        // Determine direction and iterate over squares between source and destination
        if (srcX == destX) {
            // Vertical move
            int startY = Math.min(srcY, destY) + 1;
            int endY = Math.max(srcY, destY);
            for (int y = startY; y < endY; y++) {
                if (game.getPiece(srcX, y) != null) {
                    countPiecesBetween++;
                }
            }
        } else {
            // Horizontal move
            int startX = Math.min(srcX, destX) + 1;
            int endX = Math.max(srcX, destX);
            for (int x = startX; x < endX; x++) {
                if (game.getPiece(x, srcY) != null) {
                    countPiecesBetween++;
                }
            }
        }

        Piece targetPiece = game.getPiece(move.getDestination());
        Player movingPlayer = game.getCurrentPlayer();

        if (targetPiece == null) {
            // Moving to empty square: path must be clear
            return countPiecesBetween == 0;
        } else {
            // Capturing move: must jump over exactly one piece
            // and target piece must belong to opponent
            if (targetPiece.getPlayer().equals(movingPlayer)) {
                return false; // Cannot capture own piece
            }
            return countPiecesBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}