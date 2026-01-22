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
		Place src = move.getSource();
		Place dst = move.getDestination();
		int dx = dst.x() - src.x();
		int dy = dst.y() - src.y();
		int adx = Math.abs(dx), ady = Math.abs(dy);
		if (!((adx == 2 && ady == 1) || (adx == 1 && ady == 2))) {
			return true;
		}
		Place leg;
		if (adx == 2) {
			leg = new Place(src.x() + Integer.signum(dx), src.y());
		} else {
			leg = new Place(src.x(), src.y() + Integer.signum(dy));
		}
		return game.getPiece(leg) == null;
	}
}
