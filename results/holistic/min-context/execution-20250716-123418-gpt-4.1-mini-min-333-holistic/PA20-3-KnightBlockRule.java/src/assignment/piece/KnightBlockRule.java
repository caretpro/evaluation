
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

		int dx = destination.x - source.x;
		int dy = destination.y - source.y;

		// Knight moves in an L shape: 2 in one direction and 1 in perpendicular
		// Identify the blocking square:
		// If move is 2 horizontally and 1 vertically, blocking square is 1 step horizontally from source
		// If move is 2 vertically and 1 horizontally, blocking square is 1 step vertically from source

		if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
			int blockX = source.x + dx / 2;
			int blockY = source.y;
			if (game.getPiece(new Place(blockX, blockY)) != null) {
				return false;
			}
		} else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
			int blockX = source.x;
			int blockY = source.y + dy / 2;
			if (game.getPiece(new Place(blockX, blockY)) != null) {
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