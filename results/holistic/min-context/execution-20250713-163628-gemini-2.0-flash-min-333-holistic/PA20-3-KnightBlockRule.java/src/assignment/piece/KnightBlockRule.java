
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The blocking rule applying on Knights. The rule is similar to the blocking rule for horse in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Horse'>Wikipedia</a>
 */
public class KnightBlockRule implements Rule {
    @Override
    public boolean validate(Game game, Move move) {
        if (!(game.getPiece(move.getSource()) instanceof Knight)) {
            return true;
        }

        Place source = move.getSource();
        Place destination = move.getDestination();

        int sourceRow = source.row;
        int sourceCol = source.col;
        int destRow = destination.row;
        int destCol = destination.col;

        int rowDiff = Math.abs(destRow - sourceRow);
        int colDiff = Math.abs(destCol - sourceCol);

        if (rowDiff == 2 && colDiff == 1) {
            // Vertical move of 2, horizontal move of 1
            int blockedRow = (sourceRow + destRow) / 2;
            int blockedCol = sourceCol;
            Place blockedPlace = new Place(blockedRow, blockedCol);
            if (game.getPiece(blockedPlace) != null) {
                return false; // Blocked
            }
        } else if (rowDiff == 1 && colDiff == 2) {
            // Vertical move of 1, horizontal move of 2
            int blockedRow = sourceRow;
            int blockedCol = (sourceCol + destCol) / 2;
            Place blockedPlace = new Place(blockedRow, blockedCol);
            if (game.getPiece(blockedPlace) != null) {
                return false; // Blocked
            }
        }
        return true;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}