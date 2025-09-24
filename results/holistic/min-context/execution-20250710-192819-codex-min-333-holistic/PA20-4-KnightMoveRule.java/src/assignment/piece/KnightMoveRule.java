
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
        // Only apply this rule to Knight pieces; otherwise it's not our concern
        if (!(game.getPiece(move.source()) instanceof Knight)) {
            return true;
        }

        Place from = move.source();
        Place to   = move.target();

        int rankDiff = Math.abs(to.rank() - from.rank());
        int fileDiff = Math.abs(to.file() - from.file());

        // Knight moves in an L: one axis = 2, the other = 1
        return (rankDiff == 2 && fileDiff == 1)
            || (rankDiff == 1 && fileDiff == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}