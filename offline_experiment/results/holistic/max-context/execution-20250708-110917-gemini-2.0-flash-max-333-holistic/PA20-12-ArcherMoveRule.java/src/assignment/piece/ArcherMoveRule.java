
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Archer)) {
			return true;
		}

		Place source = move.getSource();
		Place destination = move.getDestination();

		if (source.equals(destination)) {
			return false;
		}

		if (source.x() != destination.x() && source.y() != destination.y()) {
			return false;
		}

		int obstacles = 0;
		if (source.x() == destination.x()) {
			int start = Math.min(source.y(), destination.y());
			int end = Math.max(source.y(), destination.y());
			for (int i = start + 1; i < end; i++) {
				if (game.getPiece(source.x(), i) != null) {
					obstacles++;
				}
			}

		} else {
			int start = Math.min(source.x(), destination.x());
			int end = Math.max(source.x(), destination.x());
			for (int i = start + 1; i < end; i++) {
				if (game.getPiece(i, source.y()) != null) {
					obstacles++;
				}
			}
		}

		if (game.getPiece(destination) == null) {
			return obstacles == 0;
		} else {
			return obstacles == 1;
		}
	}

	@Override
	public String getDescription() {
		return "archer move rule is violated";
	}
}