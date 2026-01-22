
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
		Place target = move.getTo();

		int sourceRow = source.x();
		int sourceCol = source.y();
		int targetRow = target.x();
		int targetCol = target.y();

		int rowDiff = Math.abs(targetRow - sourceRow);
		int colDiff = Math.abs(targetCol - sourceCol);

		if (rowDiff == 2 && colDiff == 1) {
			// Vertical move of 2, horizontal move of 1
			int blockRow = (sourceRow + targetRow) / 2;
			int blockCol = sourceCol;
			if (game.getPiece(new Place(blockRow, blockCol)) != null) {
				return false; // Blocked
			}
		} else if (rowDiff == 1 && colDiff == 2) {
			// Vertical move of 1, horizontal move of 2
			int blockRow = sourceRow;
			int blockCol = (sourceCol + targetCol) / 2;
			if (game.getPiece(new Place(blockRow, blockCol)) != null) {
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