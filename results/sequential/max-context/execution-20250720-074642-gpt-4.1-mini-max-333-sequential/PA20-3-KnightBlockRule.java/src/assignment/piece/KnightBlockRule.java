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
		int diffX = dx - sx;
		int diffY = dy - sy;
		if (!((Math.abs(diffX) == 2 && Math.abs(diffY) == 1) || (Math.abs(diffX) == 1 && Math.abs(diffY) == 2))) {
			return false;
		}
		int legX, legY;
		if (Math.abs(diffX) == 2) {
			legX = sx + diffX / 2;
			legY = sy;
		} else {
			legX = sx;
			legY = sy + diffY / 2;
		}
		if (game.getPiece(legX, legY) != null) {
			return false;
		}
		return true;
	}
}
