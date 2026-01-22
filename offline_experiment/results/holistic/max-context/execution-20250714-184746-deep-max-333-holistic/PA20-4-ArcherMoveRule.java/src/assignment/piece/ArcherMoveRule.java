
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;
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
        
        Place source = move.getSource();
        Place destination = move.getDestination();
        
        // Must move orthogonally (same row or same column)
        if (source.x() != destination.x() && source.y() != destination.y()) {
            return false;
        }
        
        Piece targetPiece = game.getPiece(destination);
        int piecesBetween = countPiecesBetween(game, source, destination);
        
        // If moving to empty square (not capturing)
        if (targetPiece == null) {
            return piecesBetween == 0;
        }
        // If capturing
        else {
            return piecesBetween == 1;
        }
    }

    private int countPiecesBetween(Game game, Place source, Place destination) {
        int count = 0;
        int x1 = source.x();
        int y1 = source.y();
        int x2 = destination.x();
        int y2 = destination.y();
        
        if (x1 == x2) { // Vertical move
            int start = Math.min(y1, y2);
            int end = Math.max(y1, y2);
            for (int y = start + 1; y < end; y++) {
                if (game.getPiece(x1, y) != null) {
                    count++;
                }
            }
        } else { // Horizontal move
            int start = Math.min(x1, x2);
            int end = Math.max(x1, x2);
            for (int x = start + 1; x < end; x++) {
                if (game.getPiece(x, y1) != null) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}