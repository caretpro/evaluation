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
		if (!(game.getPiece(move.from()) instanceof Knight)) {
			return true;
		}
		Place source = move.from();
		Place target = move.to();
		int dx = target.col() - source.col();
		int dy = target.row() - source.row();
		if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
			int blockCol = source.col() + (dx > 0 ? 1 : -1);
			return game.getPiece(new Place(blockCol, source.row())) != null;
		} else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
			int blockRow = source.row() + (dy > 0 ? 1 : -1);
			return game.getPiece(new Place(source.col(), blockRow)) != null;
		}
		return false;
	}
}
