
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;

/**
 * Global rule that requires that a piece should not go to a destination where there
 * is another piece belonging to the same player.
 */
public class OccupiedRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // look up any piece at the target square
        Piece destPiece = game.pieceAt(move.row(), move.column());
        // if there's a piece there and it belongs to the same owner as the moving piece, disallow the move
        return destPiece == null
            || !destPiece.owner().equals(move.piece().owner());
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}