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
		Place target = move.getDestination();
		int sourceX = source.getCoordinateX();
		int sourceY = source.getCoordinateY();
		int targetX = target.getCoordinateX();
		int targetY = target.getCoordinateY();
		int deltaX = Math.abs(targetX - sourceX);
		int deltaY = Math.abs(targetY - sourceY);
		if ((deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2)) {
			if (deltaX == 2) {
				int blockX = (sourceX + targetX) / 2;
				Place blockPlace = new Place(blockX, sourceY);
				if (game.getPiece(blockPlace) != null) {
					return false;
				}
			} else {
				int blockY = (sourceY + targetY) / 2;
				Place blockPlace = new Place(sourceX, blockY);
				if (game.getPiece(blockPlace) != null) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
