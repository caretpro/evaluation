
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
		if (!(game.getPiece(move.getFrom()) instanceof Knight)) {
			return true;
		}

		Place source = move.getFrom();
		Place target = move.getTo();

		int dx = target.getX() - source.getX();
		int dy = target.getY() - source.getY();

		// Knight moves in an L shape: 2 in one direction and 1 in the other
		if (!((Math.abs(dx) == 2 && Math.abs(dy) == 1) || (Math.abs(dx) == 1 && Math.abs(dy) == 2))) {
			// Not a knight move, so no blocking rule applies here
			return true;
		}

		// Determine the "leg" position that must be free for the knight to move
		Place leg;
		if (Math.abs(dx) == 2) {
			// The leg is one step horizontally from source towards target
			int legX = source.getX() + dx / 2;
			int legY = source.getY();
			leg = new Place(legX, legY);
		} else {
			// The leg is one step vertically from source towards target
			int legX = source.getX();
			int legY = source.getY() + dy / 2;
			leg = new Place(legX, legY);
		}

		// If the leg position is occupied, the knight is blocked
		return game.getPiece(leg) == null;
	}

	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}
}