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
		if (!(game.getPiece(move.source()) instanceof Knight)) {
			return true;
		}
		Place src = move.source();
		Place dst = move.target();
		int dc = dst.column() - src.column();
		int dr = dst.row() - src.row();
		Place leg;
		if (Math.abs(dc) == 2 && Math.abs(dr) == 1) {
			leg = Place.of(src.row(), src.column() + Integer.signum(dc));
		} else if (Math.abs(dc) == 1 && Math.abs(dr) == 2) {
			leg = Place.of(src.row() + Integer.signum(dr), src.column());
		} else {
			return true;
		}
		return game.getPiece(leg) == null;
	}
}
