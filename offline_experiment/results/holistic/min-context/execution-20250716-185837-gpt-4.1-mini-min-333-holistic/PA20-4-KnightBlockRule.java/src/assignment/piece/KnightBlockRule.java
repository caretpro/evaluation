
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

		int dx = destination.x - source.x;
		int dy = destination.y - source.y;

		// Knight moves in an L shape: (±2, ±1) or (±1, ±2)
		if (!((Math.abs(dx) == 2 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && Math.abs(dy) == 2))) {
			return true; // Not a knight move, so no blocking rule applies here
		}

		// Determine the blocking square
		Place blockPlace;
		if (Math.abs(dx) == 2) {
			// The blocking square is one step horizontally from source in the direction of dx
			blockPlace = new Place(source.x + dx / 2, source.y);
		} else {
			// The blocking square is one step vertically from source in the direction of dy
			blockPlace = new Place(source.x, source.y + dy / 2);
		}

		// If the blocking square is occupied, the knight is blocked
		return game.getPiece(blockPlace) == null;
	}

	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}
}