
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
        
        // Must move in straight line (same row or same column)
        if (source.x() != destination.x() && source.y() != destination.y()) {
            return false;
        }
        
        int dx = Integer.compare(destination.x(), source.x());
        int dy = Integer.compare(destination.y(), source.y());
        int steps = Math.max(Math.abs(destination.x() - source.x()), 
                           Math.abs(destination.y() - source.y()));
        
        int piecesInBetween = 0;
        Place current = new Place(source.x() + dx, source.y() + dy);
        
        for (int i = 1; i < steps; i++) {
            if (game.getPiece(current) != null) {
                piecesInBetween++;
            }
            current = new Place(current.x() + dx, current.y() + dy);
        }
        
        Piece targetPiece = game.getPiece(destination);
        
        // If moving to empty square, there should be no pieces in between
        if (targetPiece == null) {
            return piecesInBetween == 0;
        }
        // If capturing, there must be exactly one piece in between
        else {
            return piecesInBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}