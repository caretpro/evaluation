
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Xiangqi#Cannon">Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        // only enforce if it's actually an Archer moving
        Piece sourcePiece = game.getPiece(move.getSource());
        if (!(sourcePiece instanceof Archer)) {
            return true;
        }

        int fromRow = move.getSourceRow();
        int fromCol = move.getSourceCol();
        int toRow   = move.getDestinationRow();
        int toCol   = move.getDestinationCol();

        // must move strictly horizontally or vertically
        if (fromRow != toRow && fromCol != toCol) {
            return false;
        }

        // count intervening pieces ("screens")
        int stepRow = Integer.compare(toRow, fromRow);
        int stepCol = Integer.compare(toCol, fromCol);
        int screens = 0;
        int r = fromRow + stepRow, c = fromCol + stepCol;
        while (r != toRow || c != toCol) {
            if (game.getPiece(r, c) != null) {
                screens++;
            }
            r += stepRow;
            c += stepCol;
        }

        Piece targetPiece = game.getPiece(move.getDestination());
        if (targetPiece == null) {
            // non-capture: path must be entirely clear
            return screens == 0;
        } else {
            // capture: must jump exactly one screen and not capture own side
            return screens == 1 && sourcePiece.side() != targetPiece.side();
        }
    }

    @Override
    public String getDescription() {
        return "archer move rule is violated";
    }
}