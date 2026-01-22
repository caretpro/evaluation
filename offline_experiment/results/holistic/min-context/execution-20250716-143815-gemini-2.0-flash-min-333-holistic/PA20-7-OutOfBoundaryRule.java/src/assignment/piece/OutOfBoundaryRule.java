
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		int rows = game.getBoard().length;
		int cols = game.getBoard()[0].length;

		int sourceRow = move.getSource().getRow();
		int sourceCol = move.getSource().getCol();
		int destRow = move.getDestination().getRow();
		int destCol = move.getDestination().getCol();

		if (sourceRow < 0 || sourceRow >= rows || sourceCol < 0 || sourceCol >= cols) {
			return false;
		}

		if (destRow < 0 || destRow >= rows || destCol < 0 || destCol >= cols) {
			return false;
		}

		return true;
	}

	@Override
	public String getDescription() {
		return "place is out of boundary of gameboard";
	}
}