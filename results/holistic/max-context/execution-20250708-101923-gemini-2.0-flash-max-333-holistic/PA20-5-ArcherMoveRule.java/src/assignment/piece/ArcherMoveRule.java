
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

		int sourceX = source.x();
		int sourceY = source.y();
		int destinationX = destination.x();
		int destinationY = destination.y();

		if (sourceX == destinationX) {
			// Vertical move
			int count = 0;
			int start = Math.min(sourceY, destinationY) + 1;
			int end = Math.max(sourceY, destinationY);
			for (int y = start; y < end; y++) {
				if (game.getPiece(sourceX, y) != null) {
					count++;
				}
			}
			if (game.getPiece(destination) == null) {
				return count == 0;
			} else {
				return count == 1;
			}
		} else if (sourceY == destinationY) {
			// Horizontal move
			int count = 0;
			int start = Math.min(sourceX, destinationX) + 1;
			int end = Math.max(sourceX, destinationX);
			for (int x = start; x < end; x++) {
				if (game.getPiece(x, sourceY) != null) {
					count++;
				}
			}
			if (game.getPiece(destination) == null) {
				return count == 0;
			} else {
				return count == 1;
			}
		}

		return false;
	}

	@Override
	public String getDescription() {
		return "archer move rule is violated";
	}
}