
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
        
        // Check if moving in straight line (orthogonal)
        if (source.x() != destination.x() && source.y() != destination.y()) {
            return false;
        }
        
        Piece destinationPiece = game.getPiece(destination);
        int piecesBetween = countPiecesBetween(game, source, destination);
        
        // For non-capture move (destination empty)
        if (destinationPiece == null) {
            return piecesBetween == 0;
        }
        // For capture move (destination has opponent's piece)
        else {
            return piecesBetween == 1 && 
                   !destinationPiece.getPlayer().equals(game.getPiece(source).getPlayer());
        }
    }

    private int countPiecesBetween(Game game, Place source, Place destination) {
        int count = 0;
        int x = source.x();
        int y = source.y();
        
        if (source.x() == destination.x()) {
            // Vertical move
            int step = source.y() < destination.y() ? 1 : -1;
            for (y += step; y != destination.y(); y += step) {
                if (game.getPiece(x, y) != null) {
                    count++;
                }
            }
        } else {
            // Horizontal move
            int step = source.x() < destination.x() ? 1 : -1;
            for (x += step; x != destination.x(); x += step) {
                if (game.getPiece(x, y) != null) {
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