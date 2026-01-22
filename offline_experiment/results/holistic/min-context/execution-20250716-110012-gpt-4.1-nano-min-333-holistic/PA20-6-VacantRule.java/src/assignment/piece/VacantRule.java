
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Position;
import assignment.protocol.Piece;

/**
 * Global rule that requires the source place of a move must have a piece on it.
 */
public class VacantRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Position sourcePosition = move.getSource();
        Piece pieceAtSource = game.getPieceAt(sourcePosition);
        return pieceAtSource != null;
    }

    @Override
    public String getDescription() {
        return "the source of move should have a piece";
    }
}