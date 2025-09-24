
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.piece.Piece;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        Piece piece = game.getPiece(move.getSource());
        if (!(piece instanceof Knight)) {
            return true; // Not a Knight, rule does not apply
        }
        int deltaX = Math.abs(move.getDestination().x() - move.getSource().x());
        int deltaY = Math.abs(move.getDestination().y() - move.getSource().y());
        // Knight moves in an "L" shape: (2,1) or (1,2)
        return (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2);
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}