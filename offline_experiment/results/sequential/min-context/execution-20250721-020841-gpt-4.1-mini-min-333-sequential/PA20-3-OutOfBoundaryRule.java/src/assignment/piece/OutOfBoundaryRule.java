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
		var board = game.getBoard();
		int height = board.length;
		int width = board[0].length;
		int sourceRow = move.getSource().row;
		int sourceCol = move.getSource().column;
		int destRow = move.getDestination().row;
		int destCol = move.getDestination().column;
		boolean sourceInBounds = sourceRow >= 0 && sourceRow < height && sourceCol >= 0 && sourceCol < width;
		boolean destInBounds = destRow >= 0 && destRow < height && destCol >= 0 && destCol < width;
		return sourceInBounds && destInBounds;
	}
}
