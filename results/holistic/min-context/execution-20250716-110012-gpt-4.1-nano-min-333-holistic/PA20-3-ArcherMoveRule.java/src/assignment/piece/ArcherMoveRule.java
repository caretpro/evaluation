
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true; // Not an Archer piece, rule does not apply
        }

        Place source = move.getSource();
        Place target = move.getDestination();

        int sourceRow = source.getRow();
        int sourceCol = source.getCol();
        int targetRow = target.getRow();
        int targetCol = target.getCol();

        // Check if move is in a straight line
        boolean isStraightLine = (sourceRow == targetRow) || (sourceCol == targetCol);
        if (!isStraightLine) {
            return false; // Move must be in a straight line
        }

        int countPiecesBetween = 0;

        if (sourceRow == targetRow) {
            // Horizontal move
            int startCol = Math.min(sourceCol, targetCol) + 1;
            int endCol = Math.max(sourceCol, targetCol);
            for (int col = startCol; col < endCol; col++) {
                if (game.getPiece(new Place(sourceRow, col)) != null) {
                    countPiecesBetween++;
                }
            }
        } else {
            // Vertical move
            int startRow = Math.min(sourceRow, targetRow) + 1;
            int endRow = Math.max(sourceRow, targetRow);
            for (int row = startRow; row < endRow; row++) {
                if (game.getPiece(new Place(row, sourceCol)) != null) {
                    countPiecesBetween++;
                }
            }
        }

        boolean isCapturing = game.getPiece(target) != null;
        if (isCapturing) {
            // To capture, there must be exactly one piece between source and target
            return countPiecesBetween == 1;
        } else {
            // To move without capturing, path must be clear
            return countPiecesBetween == 0 && game.getPiece(target) == null;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}