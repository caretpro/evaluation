
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
		
		int srcX = move.getSource().x();
		int srcY = move.getSource().y();
		int destX = move.getDestination().x();
		int destY = move.getDestination().y();

		int deltaX = destX - srcX;
		int deltaY = destY - srcY;

		// Check for valid knight move pattern: move in an "L" shape
		if (!((Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) || (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 2))) {
			// Not a valid knight move pattern
			return false;
		}

		// Determine the "leg" position that must be free
		if (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) {
			// Horizontal move: check the square between source and destination in x
			int legX = srcX + (deltaX / 2);
			int legY = srcY;
			if (game.getPiece(legX, legY) != null) {
				return false; // blocked
			}
		} else {
			// Vertical move: check the square between source and destination in y
			int legX = srcX;
			int legY = srcY + (deltaY / 2);
			if (game.getPiece(legX, legY) != null) {
				return false; // blocked
			}
		}
		return true; // move is valid if not blocked
	}

	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}
}