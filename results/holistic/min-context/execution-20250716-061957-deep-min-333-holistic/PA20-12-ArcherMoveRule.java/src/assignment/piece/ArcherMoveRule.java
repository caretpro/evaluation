
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
        
        // Check if moving orthogonally
        if (source.row() != target.row() && source.col() != target.col()) {
            return false;
        }

        boolean isCapture = game.getPiece(target) != null;
        int pieceCount = countPiecesBetween(game, source, target);

        if (isCapture) {
            // For capture, must jump exactly one piece
            return pieceCount == 1;
        } else {
            // For non-capture move, cannot jump any pieces
            return pieceCount == 0;
        }
    }

    private int countPiecesBetween(Game game, Position source, Position target) {
        int count = 0;
        int rowStep = Integer.compare(target.row(), source.row());
        int colStep = Integer.compare(target.col(), source.col());

        Position current = new Position(
            source.row() + rowStep,
            source.col() + colStep
        );

        while (!current.equals(target)) {
            if (game.getPiece(current) != null) {
                count++;
            }
            current = new Position(
                current.row() + rowStep,
                current.col() + colStep
            );
        }

        return count;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}