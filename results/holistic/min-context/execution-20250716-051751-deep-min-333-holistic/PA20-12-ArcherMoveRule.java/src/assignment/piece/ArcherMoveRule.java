
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

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
        
        // Check if moving orthogonally
        if (move.getSource().x() != move.getDestination().x() && 
            move.getSource().y() != move.getDestination().y()) {
            return false;
        }
        
        int pieceCount = countPiecesBetween(game, move);
        
        // If destination is empty, must move without jumping any pieces
        if (game.getPiece(move.getDestination()) == null) {
            return pieceCount == 0;
        }
        // If capturing, must jump exactly one piece
        else {
            return pieceCount == 1;
        }
    }

    private int countPiecesBetween(Game game, Move move) {
        int count = 0;
        int xStep = Integer.compare(move.getDestination().x(), move.getSource().x());
        int yStep = Integer.compare(move.getDestination().y(), move.getSource().y());
        
        int currentX = move.getSource().x() + xStep;
        int currentY = move.getSource().y() + yStep;
        
        while (currentX != move.getDestination().x() || currentY != move.getDestination().y()) {
            if (game.getPiece(new Move.Place(currentX, currentY)) != null) {
                count++;
            }
            currentX += xStep;
            currentY += yStep;
        }
        
        return count;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}