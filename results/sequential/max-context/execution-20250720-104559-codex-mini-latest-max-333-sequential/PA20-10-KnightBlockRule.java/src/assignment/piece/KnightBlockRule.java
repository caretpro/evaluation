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
		int dx = move.getDestination().x() - move.getSource().x();
		int dy = move.getDestination().y() - move.getSource().y();
		if (!((Math.abs(dx) == 2 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && Math.abs(dy) == 2))) {
			return true;
		}
		int eyeX = move.getSource().x() + Integer.signum(dx) * (Math.abs(dx) == 2 ? 1 : 0);
		int eyeY = move.getSource().y() + Integer.signum(dy) * (Math.abs(dy) == 2 ? 1 : 0);
		if (game.getPiece(eyeX, eyeY) != null) {
			return false;
		}
		return true;
	}
}
