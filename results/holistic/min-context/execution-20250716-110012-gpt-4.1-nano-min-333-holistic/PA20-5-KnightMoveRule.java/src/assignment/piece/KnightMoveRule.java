
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // Check if the piece at source is a Knight
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        // Retrieve source and target positions
        var source = move.getSource();
        var target = move.getTarget();
        // Calculate the absolute difference in rows and columns
        int rowDiff = Math.abs(target.getRowIndex() - source.getRowIndex());
        int colDiff = Math.abs(target.getColumnIndex() - source.getColumnIndex());
        // Knight moves in an L-shape: 2 in one direction and 1 in the other
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}