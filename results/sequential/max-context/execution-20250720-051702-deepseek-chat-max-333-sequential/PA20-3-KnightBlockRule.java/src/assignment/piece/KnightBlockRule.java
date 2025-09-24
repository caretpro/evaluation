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
		Place destination = move.getDestination();
		int dx = Math.abs(destination.x() - source.x());
		int dy = Math.abs(destination.y() - source.y());
		if (!((dx == 1 && dy == 2) || (dx == 2 && dy == 1))) {
			return true;
		}
		int blockX = source.x() + (destination.x() > source.x() ? dx / 2 : -dx / 2);
		int blockY = source.y() + (destination.y() > source.y() ? dy / 2 : -dy / 2);
		return game.getPiece(blockX, blockY) == null;
	}
}
