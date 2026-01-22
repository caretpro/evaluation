
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
		// Identify the "leg" square that must be empty:
		// If horizontal move is 2, leg is one step horizontally from source
		// If vertical move is 2, leg is one step vertically from source

		int legX, legY;

		if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
			legX = source.x() + dx / 2;
			legY = source.y();
		} else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
			legX = source.x();
			legY = source.y() + dy / 2;
		} else {
			// Not a valid knight move shape, so no blocking rule applies here
			return true;
		}

		Place legPlace = new Place(legX, legY);
		// If the leg square is occupied, knight is blocked
		if (game.getPiece(legPlace) != null) {
			return false;
		}

		return true;
	}

	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}
}