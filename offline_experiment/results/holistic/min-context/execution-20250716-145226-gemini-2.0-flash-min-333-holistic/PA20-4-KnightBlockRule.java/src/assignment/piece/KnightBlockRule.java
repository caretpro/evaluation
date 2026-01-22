
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

        if (source == null || destination == null) {
            return false;
        }

        int sourceRow = source.getRow();
        int sourceCol = source.getCol();
        int destRow = destination.getRow();
        int destCol = destination.getCol();

        int rowDiff = Math.abs(destRow - sourceRow);
        int colDiff = Math.abs(destCol - sourceCol);

        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            if (rowDiff == 2) {
                int blockRow = (sourceRow + destRow) / 2;
                int blockCol = sourceCol;
                Place blockPlace = new Place(blockRow, blockCol);
                if (game.getPiece(blockPlace) != null) {
                    return false;
                }
            } else {
                int blockRow = sourceRow;
                int blockCol = (sourceCol + destCol) / 2;
                Place blockPlace = new Place(blockRow, blockCol);
                if (game.getPiece(blockPlace) != null) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public String getDescription() {
        return "knight is blocked by another piece";
    }
}