
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
        if (!(game.getPiece(move.from()) instanceof Archer)) {
            return true;
        }

        // Check if moving in straight line (rook-like movement)
        if (move.from().row() != move.to().row() && 
            move.from().column() != move.to().column()) {
            return false;
        }
        
        int pieceCount = countPiecesBetween(game, move.from(), move.to());
        
        // If target is empty, must move without jumping any pieces
        if (game.getPiece(move.to()) == null) {
            return pieceCount == 0;
        }
        // If target is occupied, must jump exactly one piece
        else {
            return pieceCount == 1;
        }
    }

    private int countPiecesBetween(Game game, Move.Place from, Move.Place to) {
        int count = 0;
        int rowStep = Integer.compare(to.row(), from.row());
        int colStep = Integer.compare(to.column(), from.column());
        
        Move.Place current = new Move.Place(
            from.row() + rowStep,
            from.column() + colStep
        );
        
        while (!current.equals(to)) {
            if (game.getPiece(current) != null) {
                count++;
            }
            current = new Move.Place(
                current.row() + rowStep,
                current.column() + colStep
            );
        }
        
        return count;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}