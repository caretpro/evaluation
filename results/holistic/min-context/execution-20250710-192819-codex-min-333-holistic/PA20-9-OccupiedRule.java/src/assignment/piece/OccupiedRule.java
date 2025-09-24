
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Player;

/**
 * Global rule that requires that a piece should not go to a destination where there is another piece belonging to the
 * same player.
 */
public class OccupiedRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // retrieve the moving piece and any piece at the target square
        Piece movingPiece = game.getPieceAt(move.from());
        Piece targetPiece = game.getPieceAt(move.to());
        Player movingPlayer = movingPiece.owner();

        // valid if the destination is empty or occupied by an opponent's piece
        return targetPiece == null || !targetPiece.owner().equals(movingPlayer);
    }

    @Override
    public String getDescription() {
        return "piece cannot be captured by another piece belonging to the same player";
    }
}