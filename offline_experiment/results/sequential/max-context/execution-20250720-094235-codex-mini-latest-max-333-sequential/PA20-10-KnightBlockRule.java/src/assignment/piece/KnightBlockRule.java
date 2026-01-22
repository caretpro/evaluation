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
		int deltaX = dx - sx;
		int deltaY = dy - sy;
		if (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) {
			int blockX = sx + deltaX / 2;
			int blockY = sy;
			if (game.getPiece(new Place(blockX, blockY)) != null) {
				return false;
			}
		} else if (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 2) {
			int blockX = sx;
			int blockY = sy + deltaY / 2;
			if (game.getPiece(new Place(blockX, blockY)) != null) {
				return false;
			}
		}
		return true;
	}
}
