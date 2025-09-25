package assignment.piece;

import assignment.piece.Rule;
import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * Global rule that requires the source place of a move must have a piece on it.
 */
public class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // TODO student implementation
        return false;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}
