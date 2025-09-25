package assignment.piece;

import assignment.piece.Archer;
import assignment.piece.Rule;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 * Cannons move like chariots, any distance orthogonally without jumping, but can only capture by jumping a single piece of either colour along the path of attack.
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Archer)) {
            return true;
        }
        // TODO student implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}
