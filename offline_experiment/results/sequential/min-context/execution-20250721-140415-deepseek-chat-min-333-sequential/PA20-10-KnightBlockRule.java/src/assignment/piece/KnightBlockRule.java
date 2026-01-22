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
		Place source = move.source();
		Place target = move.destination();
		int dx = target.x() - source.x();
		int dy = target.y() - source.y();
		if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
			int blockX = source.x() + dx / 2;
			return game.getPiece(new Place(blockX, source.y())) != null;
		} else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
			int blockY = source.y() + dy / 2;
			return game.getPiece(new Place(source.x(), blockY)) != null;
		}
		return false;
	}
}
