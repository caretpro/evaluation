
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
            return true;
        }

        Place source = move.getSource();
        Place target = move.getTarget();

        if (source == null || target == null) {
            return false;
        }

        int sourceRow = source.getRow();
        int sourceCol = source.getCol();
        int targetRow = target.getRow();
        int targetCol = target.getCol();

        int rowDiff = Math.abs(sourceRow - targetRow);
        int colDiff = Math.abs(sourceCol - targetCol);

        if ((rowDiff == 0 && colDiff == 0) || (rowDiff != 0 && colDiff != 0)) {
            return false;
        }

        int count = 0;
        if (rowDiff == 0) {
            int start = Math.min(sourceCol, targetCol) + 1;
            int end = Math.max(sourceCol, targetCol);
            for (int col = start; col < end; col++) {
                if (game.getPiece(sourceRow, col) != null) {
                    count++;
                }
            }
        } else {
            int start = Math.min(sourceRow, targetRow) + 1;
            int end = Math.max(sourceRow, targetRow);
            for (int row = start; row < end; row++) {
                if (game.getPiece(row, sourceCol) != null) {
                    count++;
                }
            }
        }

        if (game.getPiece(target) == null) {
            return count == 0;
        } else {
            return count == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}