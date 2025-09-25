package assignment.piece;

import assignment.piece.Knight;
import assignment.piece.Rule;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * The blocking rule applying on Knights. The rule is similar to the blocking rule for horse in Chinese chess.
 * The horse does not jump as the knight does in Western chess, and can be blocked by a piece of either colour located one point horizontally or vertically adjacent to it
 */
public class KnightBlockRule implements Rule {
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
        return "knight is blocked by another piece";
    }
}
