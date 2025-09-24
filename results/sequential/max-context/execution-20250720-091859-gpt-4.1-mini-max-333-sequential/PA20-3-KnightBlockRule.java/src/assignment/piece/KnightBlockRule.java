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
		int sx = move.getSource().x();
		int sy = move.getSource().y();
		int dx = move.getDestination().x();
		int dy = move.getDestination().y();
		int dxAbs = Math.abs(dx - sx);
		int dyAbs = Math.abs(dy - sy);
		if (!((dxAbs == 2 && dyAbs == 1) || (dxAbs == 1 && dyAbs == 2))) {
			return false;
		}
		int legX, legY;
		if (dxAbs == 2 && dyAbs == 1) {
			legX = sx + (dx - sx) / 2;
			legY = sy;
		} else {
			legX = sx;
			legY = sy + (dy - sy) / 2;
		}
		if (game.getPiece(legX, legY) != null) {
			return false;
		}
		return true;
	}
}
