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
		if (!(game.getPiece(move.source) instanceof Knight)) {
			return true;
		}
		Place source = move.source;
		Place target = move.target;
		int dx = target.x - source.x;
		int dy = target.y - source.y;
		if (Math.abs(dx) + Math.abs(dy) != 3 || Math.abs(dx) == 0 || Math.abs(dy) == 0) {
			return false;
		}
		int blockX = source.x;
		int blockY = source.y;
		if (Math.abs(dx) == 2) {
			blockX += dx / 2;
		} else {
			blockY += dy / 2;
		}
		return game.getPiece(new Place(blockX, blockY)) == null;
	}
}
