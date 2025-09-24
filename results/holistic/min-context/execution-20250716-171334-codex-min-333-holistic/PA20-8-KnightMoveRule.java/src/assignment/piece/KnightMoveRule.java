
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // only validate when the moving piece is actually a Knight
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        int srcRow = move.getSourceRow();
        int srcCol = move.getSourceColumn();
        int tgtRow = move.getTargetRow();
        int tgtCol = move.getTargetColumn();

        int dr = Math.abs(tgtRow - srcRow);
        int dc = Math.abs(tgtCol - srcCol);

        // knight moves exactly two in one direction and one in the other
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}