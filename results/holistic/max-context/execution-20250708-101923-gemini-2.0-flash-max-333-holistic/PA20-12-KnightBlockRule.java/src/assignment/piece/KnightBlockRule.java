
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
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Knight)) {
			return true;
		}
		Place source = move.getSource();
		Place destination = move.getDestination();

		int dx = Math.abs(destination.x() - source.x());
		int dy = Math.abs(destination.y() - source.y());

		if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
			int blockX = -1, blockY = -1;

			if (dx == 2) {
				blockX = (source.x() + destination.x()) / 2;
				blockY = source.y();
			} else {
				blockX = source.x();
				blockY = (source.y() + destination.y()) / 2;
			}

			if (game.getPiece(blockX, blockY) != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}
}