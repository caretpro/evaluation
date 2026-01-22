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
		int[] size = game.getSize();
		int rows = size[0], cols = size[1];
		int[] from = move.getFrom();
		int[] to = move.getTo();
		return from[0] >= 0 && from[0] < rows && from[1] >= 0 && from[1] < cols && to[0] >= 0 && to[0] < rows
				&& to[1] >= 0 && to[1] < cols;
	}
}
