
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Position;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getFrom()) instanceof Archer)) {
            return true;
        }

        Position source = move.getFrom();
        Position target = move.getTo();
        
        // Must move in straight line (same row or same column)
        if (source.getRow() != target.getRow() && source.getColumn() != target.getColumn()) {
            return false;
        }
        
        boolean isCapture = game.getPiece(target) != null;
        int piecesBetween = countPiecesBetween(game, source, target);
        
        if (isCapture) {
            // For capture, must have exactly one piece between
            return piecesBetween == 1;
        } else {
            // For non-capture, must have no pieces between
            return piecesBetween == 0;
        }
    }

    private int countPiecesBetween(Game game, Position source, Position target) {
        int count = 0;
        int rowStep = Integer.signum(target.getRow() - source.getRow());
        int colStep = Integer.signum(target.getColumn() - source.getColumn());
        
        Position current = new Position(
            source.getRow() + rowStep,
            source.getColumn() + colStep
        );
        
        while (!current.equals(target)) {
            if (game.getPiece(current) != null) {
                count++;
            }
            current = new Position(
                current.getRow() + rowStep,
                current.getColumn() + colStep
            );
        }
        
        return count;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}