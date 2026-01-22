
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {

    @Override
    public boolean validate(Game game, Move move) {
        // only apply if the moving piece is an Archer
        int srcRow = move.getSourceRow();
        int srcCol = move.getSourceCol();
        Piece srcPiece = game.getPiece(srcRow, srcCol);
        if (!(srcPiece instanceof Archer)) {
            return true;
        }

        int dstRow = move.getDestinationRow();
        int dstCol = move.getDestinationCol();

        // must move straight (same row or same column)
        if (srcRow != dstRow && srcCol != dstCol) {
            return false;
        }

        // collect pieces between source and destination (exclusive)
        List<Piece> between = new ArrayList<>();
        if (srcRow == dstRow) {
            int start = Math.min(srcCol, dstCol) + 1;
            int end   = Math.max(srcCol, dstCol);
            for (int c = start; c < end; c++) {
                Piece p = game.getPiece(srcRow, c);
                if (p != null) {
                    between.add(p);
                }
            }
        } else {
            int start = Math.min(srcRow, dstRow) + 1;
            int end   = Math.max(srcRow, dstRow);
            for (int r = start; r < end; r++) {
                Piece p = game.getPiece(r, srcCol);
                if (p != null) {
                    between.add(p);
                }
            }
        }

        Piece destPiece = game.getPiece(dstRow, dstCol);
        if (destPiece == null) {
            // non‑capturing move: no pieces in between
            return between.isEmpty();
        } else {
            // capturing move: target must be opponent's piece, and exactly one piece in between
            if (destPiece.owner() == srcPiece.owner()) {
                return false;
            }
            return between.size() == 1;
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}