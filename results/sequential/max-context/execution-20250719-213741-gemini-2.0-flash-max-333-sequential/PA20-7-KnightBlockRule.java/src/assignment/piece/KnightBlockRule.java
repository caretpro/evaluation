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
		int sourceX = move.getSource().x();
		int sourceY = move.getSource().y();
		int destX = move.getDestination().x();
		int destY = move.getDestination().y();
		int dx = Math.abs(destX - sourceX);
		int dy = Math.abs(destY - sourceY);
		if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
			if (dx == 2) {
				int blockX = (sourceX + destX) / 2;
				if (game.getPiece(blockX, sourceY) != null) {
					return false;
				}
			} else {
				int blockY = (sourceY + destY) / 2;
				if (game.getPiece(sourceX, blockY) != null) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
