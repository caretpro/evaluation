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
		int srcRow = move.getSource().getRow();
		int srcCol = move.getSource().getCol();
		int destRow = move.getDestination().getRow();
		int destCol = move.getDestination().getCol();
		int deltaRow = destRow - srcRow;
		int deltaCol = destCol - srcCol;
		if (Math.abs(deltaRow) == 2 && Math.abs(deltaCol) == 1) {
			int legRow = srcRow + (deltaRow / 2);
			int legCol = srcCol;
			if (game.getPiece(new Place(legRow, legCol)) != null) {
				return false;
			}
		} else if (Math.abs(deltaCol) == 2 && Math.abs(deltaRow) == 1) {
			int legRow = srcRow;
			int legCol = srcCol + (deltaCol / 2);
			if (game.getPiece(new Place(legRow, legCol)) != null) {
				return false;
			}
		}
		return true;
	}
}
