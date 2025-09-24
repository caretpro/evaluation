
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
		Place target = move.getTarget();

		int sourceRow = source.getRow();
		int sourceCol = source.getCol();
		int targetRow = target.getRow();
		int targetCol = target.getCol();

		int rowDiff = Math.abs(targetRow - sourceRow);
		int colDiff = Math.abs(targetCol - sourceCol);

		// Knight moves 2 squares in one direction and 1 square in the other
		if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
			// Check for blocking piece
			if (rowDiff == 2) {
				int blockedRow = (sourceRow + targetRow) / 2;
				int blockedCol = sourceCol;
				if (game.getPiece(new Place(blockedRow, blockedCol)) != null) {
					return false; // Blocked
				}
			} else { // colDiff == 2
				int blockedRow = sourceRow;
				int blockedCol = (sourceCol + targetCol) / 2;
				if (game.getPiece(new Place(blockedRow, blockedCol)) != null) {
					return false; // Blocked
				}
			}
			return true; // Not blocked
		}

		return false;
	}

	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}
}