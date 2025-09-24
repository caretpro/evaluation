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
		int rows = game.getRows();
		int cols = game.getColumns();
		int srcRow = move.getSrcRow();
		int srcCol = move.getSrcColumn();
		int dstRow = move.getDstRow();
		int dstCol = move.getDstColumn();
		return srcRow >= 0 && srcRow < rows && srcCol >= 0 && srcCol < cols && dstRow >= 0 && dstRow < rows
				&& dstCol >= 0 && dstCol < cols;
	}
}
