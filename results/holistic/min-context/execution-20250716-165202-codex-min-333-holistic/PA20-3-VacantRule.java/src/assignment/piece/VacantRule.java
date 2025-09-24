
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Rule;
import assignment.protocol.Position;

/**
 * Global rule that requires the source place of a move must have a piece on it.
 */
public class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Position from = move.getFrom();
        return game.getPieceAt(from.row(), from.column()) != null;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}