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
		Place source = move.getSource();
		Place dest = move.getDestination();
		int dx = dest.x() - source.x();
		int dy = dest.y() - source.y();
		if (!(Math.abs(dx) == 2 && Math.abs(dy) == 1) && !(Math.abs(dx) == 1 && Math.abs(dy) == 2)) {
			return true;
		}
		Place blockingPos;
		if (Math.abs(dx) > Math.abs(dy)) {
			blockingPos = new Place(source.x() + dx / 2, source.y());
		} else {
			blockingPos = new Place(source.x(), source.y() + dy / 2);
		}
		return game.getPiece(blockingPos) == null;
	}
}
