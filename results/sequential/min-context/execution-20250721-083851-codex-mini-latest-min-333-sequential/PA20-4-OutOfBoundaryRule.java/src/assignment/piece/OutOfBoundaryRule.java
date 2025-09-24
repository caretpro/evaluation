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
		int rows = game.getBoard().getRows();
		int cols = game.getBoard().getColumns();
		int sr = move.getFrom().getRow();
		int sc = move.getFrom().getColumn();
		int dr = move.getTo().getRow();
		int dc = move.getTo().getColumn();
		return sr >= 0 && sr < rows && sc >= 0 && sc < cols && dr >= 0 && dr < rows && dc >= 0 && dc < cols;
	}
}
