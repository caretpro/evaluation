
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // if it's not a Knight, this rule does not apply
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        int dx = Math.abs(move.getDestination().x() - move.getSource().x());
        int dy = Math.abs(move.getDestination().y() - move.getSource().y());
        // knight moves in an L: (2,1) or (1,2)
        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}