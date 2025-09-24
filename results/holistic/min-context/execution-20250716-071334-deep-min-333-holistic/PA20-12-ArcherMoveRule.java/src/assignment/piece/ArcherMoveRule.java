
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

        Position source = move.from();
        Position target = move.to();
        
        // Must move in straight line (same row or same column)
        if (source.row() != target.row() && source.column() != target.column()) {
            return false;
        }

        int pieceCount = countPiecesBetween(game, source, target);
        
        // For non-capture moves, must have no pieces in between (like a rook)
        if (game.getPiece(target) == null) {
            return pieceCount == 0;
        }
        // For capture moves, must jump over exactly one piece
        else {
            return pieceCount == 1;
        }
    }

    private int countPiecesBetween(Game game, Position source, Position target) {
        int count = 0;
        int rowStep = Integer.compare(target.row(), source.row());
        int colStep = Integer.compare(target.column(), source.column());
        
        Position current = new Position(
            source.row() + rowStep,
            source.column() + colStep
        );
        
        while (!current.equals(target)) {
            if (game.getPiece(current) != null) {
                count++;
            }
            current = new Position(
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

    record Position(int row, int column) {}
}