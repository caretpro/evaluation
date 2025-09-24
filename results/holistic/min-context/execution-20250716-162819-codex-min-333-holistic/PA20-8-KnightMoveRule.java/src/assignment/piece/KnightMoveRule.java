
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
        // only apply this rule to Knight pieces
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place src = move.getSource();
        Place dst = move.getDestination();

        // Convert rank (1–8) to 0–7 and file ('a'–'h') to 0–7
        int srcRow = src.rank() - 1;
        int srcCol = src.file() - 'a';
        int dstRow = dst.rank() - 1;
        int dstCol = dst.file() - 'a';

        int deltaRow = Math.abs(dstRow - srcRow);
        int deltaCol = Math.abs(dstCol - srcCol);

        // valid if (2,1) or (1,2)
        return (deltaRow == 2 && deltaCol == 1)
            || (deltaRow == 1 && deltaCol == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}