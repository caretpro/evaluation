
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        Place source = move.getSource();
        if (!(game.getPiece(source) instanceof Knight)) {
            return true;
        }
        Place target = move.getTarget();

        int sourceRow = source.row;
        int sourceCol = source.col;
        int targetRow = target.row;
        int targetCol = target.col;

        int rowDiff = Math.abs(targetRow - sourceRow);
        int colDiff = Math.abs(targetCol - sourceCol);

        // Knight moves in an L shape: 2 by 1 or 1 by 2
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}