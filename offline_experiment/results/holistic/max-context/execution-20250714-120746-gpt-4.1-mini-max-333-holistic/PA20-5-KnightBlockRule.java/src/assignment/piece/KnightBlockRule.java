
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

		int dx = destination.x() - source.x();
		int dy = destination.y() - source.y();

		// Knight moves in L shape: (±2, ±1) or (±1, ±2)
		// Identify the "leg" position that must be empty for the Knight to move
		int legX, legY;

		if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
			legX = source.x() + dx / 2;
			legY = source.y();
		} else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
			legX = source.x();
			legY = source.y() + dy / 2;
		} else {
			// Not a valid Knight move shape, let other rules handle it
			return true;
		}

		// Check if leg position is within board boundaries
		int size = game.getConfiguration().getSize();
		if (legX < 0 || legX >= size || legY < 0 || legY >= size) {
			// Out of bounds leg position means invalid move, but this rule does not block it
			return true;
		}

		Place leg = new Place(legX, legY);
		// If the leg position is occupied, the Knight is blocked
		if (game.getPiece(leg) != null) {
			return false;
		}

		return true;
	}

	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}
}