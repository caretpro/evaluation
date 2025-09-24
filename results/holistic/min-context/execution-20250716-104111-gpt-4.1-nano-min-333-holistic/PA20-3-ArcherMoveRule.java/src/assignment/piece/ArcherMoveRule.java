
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
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }

        int srcRow = move.getSource().getRow();
        int srcCol = move.getSource().getCol();
        int destRow = move.getDestination().getRow();
        int destCol = move.getDestination().getCol();

        // Check if move is in a straight line
        if (srcRow != destRow && srcCol != destCol) {
            return false; // Not a straight line
        }

        int countPiecesInBetween = 0;

        // Determine direction and iterate through the path
        if (srcRow == destRow) {
            // Horizontal move
            int start = Math.min(srcCol, destCol) + 1;
            int end = Math.max(srcCol, destCol);
            for (int col = start; col < end; col++) {
                if (game.getPieceAt(srcRow, col) != null) {
                    countPiecesInBetween++;
                }
            }
        } else {
            // Vertical move
            int start = Math.min(srcRow, destRow) + 1;
            int end = Math.max(srcRow, destRow);
            for (int row = start; row < end; row++) {
                if (game.getPieceAt(row, srcCol) != null) {
                    countPiecesInBetween++;
                }
            }
        }

        // Check destination piece
        boolean isCapturing = game.getPiece(move.getDestination()) != null;
        if (isCapturing) {
            // Must jump over exactly one piece
            return countPiecesInBetween == 1;
        } else {
            // Moving without capturing: path must be clear
            return countPiecesInBetween == 0;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}