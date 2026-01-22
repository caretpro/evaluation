
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Position;
import assignment.protocol.Rule;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Xiangqi#Cannon">Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // only validate Archer moves; non-Archers are handled elsewhere
        Piece srcPiece = game.getPiece(move.getSource());
        if (!(srcPiece instanceof Archer)) {
            return true;
        }

        Position from = move.getSource();
        Position to   = move.getTarget();

        int dx = Integer.compare(to.getCol(), from.getCol());
        int dy = Integer.compare(to.getRow(), from.getRow());

        // must move strictly horizontally or vertically
        if (dx != 0 && dy != 0) {
            return false;
        }

        // count intervening pieces ("screens") between from and to (exclusive)
        int screens = 0;
        Position curr = from.offset(dy, dx);
        while (!curr.equals(to)) {
            if (!(game.getPiece(curr) instanceof assignment.protocol.Empty)) {
                screens++;
            }
            curr = curr.offset(dy, dx);
        }

        boolean isCapture = !(game.getPiece(to) instanceof assignment.protocol.Empty);
        // capture: exactly one screen; non‑capture: no screens
        return isCapture ? (screens == 1) : (screens == 0);
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}