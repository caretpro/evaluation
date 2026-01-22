
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
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        
        Place source = move.getSource();
        Place target = move.getTarget();
        int dx = Math.abs(target.getX() - source.getX());
        int dy = Math.abs(target.getY() - source.getY());
        
        // Knight moves in L-shape: (2,1) or (1,2)
        return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}