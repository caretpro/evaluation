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
		int dr = dst.row() - src.row();
		int dc = dst.col() - src.col();
		if (!((Math.abs(dr) == 2 && Math.abs(dc) == 1) || (Math.abs(dr) == 1 && Math.abs(dc) == 2))) {
			return true;
		}
		Place leg;
		if (Math.abs(dr) == 2) {
			leg = new Place(src.row() + Integer.signum(dr), src.col());
		} else {
			leg = new Place(src.row(), src.col() + Integer.signum(dc));
		}
		return game.getPiece(leg) == null;
	}
}
