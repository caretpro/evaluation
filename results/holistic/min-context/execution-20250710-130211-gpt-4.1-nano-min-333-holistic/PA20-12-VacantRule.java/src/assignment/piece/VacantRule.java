
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * Global rule that requires the source place of a move must have a piece on it.
 */
public class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // Check if the source position has a piece
        return game.getPieceAt(move.getSource()) != null;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}