package assignment.piece;

import assignment.piece.Knight;
import assignment.piece.Rule;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * Moving rule of Knight in chess (no block)
 * It moves two squares vertically and one square horizontally, or two squares horizontally and one square vertically
 */
public class KnightMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }
        // TODO student implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "knight move rule is violated";
    }
}
