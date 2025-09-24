
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
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Archer)) {
            return true; // Not an Archer, rule does not apply
        }

        int srcX = move.getSource().x();
        int srcY = move.getSource().y();
        int destX = move.getDestination().x();
        int destY = move.getDestination().y();

        // Check if move is along a straight line
        if (srcX != destX && srcY != destY) {
            return false; // Move is not straight
        }

        int countBetween = 0;
        int stepX = Integer.compare(destX, srcX);
        int stepY = Integer.compare(destY, srcY);

        int currentX = srcX + stepX;
        int currentY = srcY + stepY;

        // Count pieces between source and destination
        while (currentX != destX || currentY != destY) {
            if (game.getPiece(currentX, currentY) != null) {
                countBetween++;
            }
            currentX += stepX;
            currentY += stepY;
        }

        Piece destPiece = game.getPiece(move.getDestination());
        boolean isCapturing = destPiece != null && destPiece.getPlayer() != game.getCurrentPlayer();

        // Cannot capture own piece
        if (destPiece != null && destPiece.getPlayer() == game.getCurrentPlayer()) {
            return false;
        }

        // For non-capturing move, path must be clear
        if (!isCapturing && countBetween != 0) {
            return false;
        }

        // For capturing move, exactly one piece must be in between
        if (isCapturing && countBetween != 1) {
            return false;
        }

        return true;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}