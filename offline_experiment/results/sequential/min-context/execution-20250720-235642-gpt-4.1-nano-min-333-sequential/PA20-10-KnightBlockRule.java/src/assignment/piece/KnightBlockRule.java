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
	public String getDescription() {
		return "knight is blocked by another piece";
	}

	@Override
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Knight)) {
			return true;
		}
		int sourceRow = move.getSource().getRow();
		int sourceCol = move.getSource().getCol();
		int destRow = move.getDestination().getRow();
		int destCol = move.getDestination().getCol();
		int rowDiff = destRow - sourceRow;
		int colDiff = destCol - sourceCol;
		if (!((Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1)
				|| (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2))) {
			return true;
		}
		int legRow, legCol;
		if (Math.abs(rowDiff) == 2) {
			legRow = sourceRow + (rowDiff / 2);
			legCol = sourceCol;
		} else {
			legRow = sourceRow;
			legCol = sourceCol + (colDiff / 2);
		}
		Place legPosition = new Place(legRow, legCol);
		if (game.getPiece(legPosition) != null) {
			return false;
		}
		return true;
	}
}
