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
		int sourceRow = move.getSource().row;
		int sourceCol = move.getSource().col;
		int targetRow = move.getDestination().row;
		int targetCol = move.getDestination().col;
		int rowDiff = targetRow - sourceRow;
		int colDiff = targetCol - sourceCol;
		if (!((Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1)
				|| (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2))) {
			return true;
		}
		int legRow = sourceRow + (rowDiff == 2 ? 1 : (rowDiff == -2 ? -1 : 0));
		int legCol = sourceCol + (colDiff == 2 ? 1 : (colDiff == -2 ? -1 : 0));
		if (game.getPiece(new Place(legRow, legCol)) != null) {
			return false;
		}
		return true;
	}
}
