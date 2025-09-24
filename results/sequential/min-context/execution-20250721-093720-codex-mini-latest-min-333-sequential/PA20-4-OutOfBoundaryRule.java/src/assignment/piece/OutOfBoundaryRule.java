package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
	@Override
	public String getDescription() {
		return "place is out of boundary of gameboard";
	}

	@Override
	public boolean validate(Game game, Move move) {
		int rows = game.getSize().getRowCount();
		int cols = game.getSize().getColumnCount();
		int r1 = move.getSource().getRow();
		int c1 = move.getSource().getColumn();
		int r2 = move.getTarget().getRow();
		int c2 = move.getTarget().getColumn();
		return r1 >= 0 && r1 < rows && c1 >= 0 && c1 < cols && r2 >= 0 && r2 < rows && c2 >= 0 && c2 < cols;
	}
}
