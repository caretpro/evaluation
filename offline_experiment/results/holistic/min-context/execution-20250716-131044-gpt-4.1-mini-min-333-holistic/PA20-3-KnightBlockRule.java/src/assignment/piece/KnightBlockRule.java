
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
		Place target = move.getDestination();

		int dx = target.x() - source.x();
		int dy = target.y() - source.y();

		// Knight moves in an L shape: 2 in one direction and 1 in the other
		// Check the blocking position depending on the move direction
		if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
			// The blocking position is one step in the horizontal direction from source
			int blockX = source.x() + dx / 2;
			int blockY = source.y();
			if (game.getPiece(new Place(blockX, blockY)) != null) {
				return false;
			}
		} else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
			// The blocking position is one step in the vertical direction from source
			int blockX = source.x();
			int blockY = source.y() + dy / 2;
			if (game.getPiece(new Place(blockX, blockY)) != null) {
				return false;
			}
		} else {
			// Not a valid knight move, but this rule only checks blocking, so allow it here
			return true;
		}

		return true;
	}

	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}
}