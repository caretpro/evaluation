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
		int dx = Math.abs(dest.x() - source.x());
		int dy = Math.abs(dest.y() - source.y());
		if (!((dx == 1 && dy == 2) || (dx == 2 && dy == 1))) {
			return true;
		}
		int blockX = source.x();
		int blockY = source.y();
		if (dx == 2) {
			blockX += (dest.x() > source.x()) ? 1 : -1;
		} else {
			blockY += (dest.y() > source.y()) ? 1 : -1;
		}
		return game.getPiece(blockX, blockY) == null;
	}
}
