
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		int rows = game.getBoardRows();
		int cols = game.getBoardCols();

		int sourceRow = move.getSource().row();
		int sourceCol = move.getSource().col();
		int destRow = move.getDestination().row();
		int destCol = move.getDestination().col();

		if (sourceRow < 0 || sourceRow >= rows || sourceCol < 0 || sourceCol >= cols ||
			destRow < 0 || destRow >= rows || destCol < 0 || destCol >= cols) {
			return false;
		}

		return true;
	}

	@Override
	public String getDescription() {
		return "place is out of boundary of gameboard";
	}
}