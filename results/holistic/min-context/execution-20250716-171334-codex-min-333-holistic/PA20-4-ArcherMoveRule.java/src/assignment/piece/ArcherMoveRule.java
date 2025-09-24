
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        Piece srcPiece = game.getPiece(move.getSource());
        if (!(srcPiece instanceof Archer)) {
            return true;
        }
        Place from = move.getSource();
        Place to   = move.getDestination();

        int dCol = Integer.signum(to.col() - from.col());
        int dRow = Integer.signum(to.row() - from.row());

        // must move in a straight line
        if (dCol != 0 && dRow != 0) {
            return false;
        }

        int steps = Math.max(
            Math.abs(to.col() - from.col()),
            Math.abs(to.row() - from.row())
        );
        int jumpCount = 0;
        for (int step = 1; step < steps; step++) {
            Place between = from.offset(dCol * step, dRow * step);
            if (game.getPiece(between) != null) {
                jumpCount++;
            }
        }

        Piece destPiece = game.getPiece(to);
        if (destPiece == null) {
            // non-capturing move: path must be clear (no jumps)
            return jumpCount == 0;
        } else {
            // capturing move: must jump exactly one piece and not capture own piece
            return jumpCount == 1
                && destPiece.owner() != srcPiece.owner();
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}