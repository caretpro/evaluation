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
		int blockX = -1;
		int blockY = -1;
		if (destX == sourceX + 2 && destY == sourceY + 1) {
			blockX = sourceX + 1;
			blockY = sourceY;
		} else if (destX == sourceX + 2 && destY == sourceY - 1) {
			blockX = sourceX + 1;
			blockY = sourceY;
		} else if (destX == sourceX - 2 && destY == sourceY + 1) {
			blockX = sourceX - 1;
			blockY = sourceY;
		} else if (destX == sourceX - 2 && destY == sourceY - 1) {
			blockX = sourceX - 1;
			blockY = sourceY;
		} else if (destX == sourceX + 1 && destY == sourceY + 2) {
			blockX = sourceX;
			blockY = sourceY + 1;
		} else if (destX == sourceX - 1 && destY == sourceY + 2) {
			blockX = sourceX;
			blockY = sourceY + 1;
		} else if (destX == sourceX + 1 && destY == sourceY - 2) {
			blockX = sourceX;
			blockY = sourceY - 1;
		} else if (destX == sourceX - 1 && destY == sourceY - 2) {
			blockX = sourceX;
			blockY = sourceY - 1;
		}
		if (blockX != -1 && blockY != -1 && game.getPiece(blockX, blockY) != null) {
			return false;
		}
		return true;
	}
}
