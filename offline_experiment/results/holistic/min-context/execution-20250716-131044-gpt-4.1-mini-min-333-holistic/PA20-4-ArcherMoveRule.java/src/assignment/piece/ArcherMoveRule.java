
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.source());
        if (!(piece instanceof Archer)) {
            return true;
        }

        Place source = move.source();
        Place target = move.target();

        // Move must be strictly horizontal or vertical
        if (source.row() != target.row() && source.col() != target.col()) {
            return false;
        }

        int countBetween = 0;

        if (source.row() == target.row()) {
            // Horizontal move
            int row = source.row();
            int startCol = Math.min(source.col(), target.col()) + 1;
            int endCol = Math.max(source.col(), target.col());

            for (int col = startCol; col < endCol; col++) {
                if (game.getPiece(new Place(row, col)) != null) {
                    countBetween++;
                }
            }
        } else {
            // Vertical move
            int col = source.col();
            int startRow = Math.min(source.row(), target.row()) + 1;
            int endRow = Math.max(source.row(), target.row());

            for (int row = startRow; row < endRow; row++) {
                if (game.getPiece(new Place(row, col)) != null) {
                    countBetween++;
                }
            }
        }

        Piece targetPiece = game.getPiece(target);

        if (targetPiece == null) {
            // Moving without capture: no pieces between source and target
            return countBetween == 0;
        } else {
            // Capture move: exactly one piece between source and target
            // Also, cannot capture own piece
            if (piece.owner() == targetPiece.owner()) {
                return false;
            }
            return countBetween == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}