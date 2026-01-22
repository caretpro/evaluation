package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source place of a move must have a piece on it.
 */
public class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        var sourcePiece = game.getPiece(move.getSource());
        return sourcePiece != null;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}
