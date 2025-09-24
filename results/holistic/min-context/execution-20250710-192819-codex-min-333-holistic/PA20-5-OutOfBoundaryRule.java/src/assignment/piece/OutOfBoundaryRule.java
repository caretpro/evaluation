
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.piece.OutOfBoundaryRule;  // <-- add this import

/**
 * Archer piece implementation.
 */
public class Archer implements Piece {
    // … your existing fields …

    @Override
    public boolean validateMove(Game game, Move move) {
        // apply out‑of‑boundary check first
        if (!new OutOfBoundaryRule().validate(game, move)) {
            return false;
        }
        // … rest of your validation logic …
    }

    // … other methods …
}