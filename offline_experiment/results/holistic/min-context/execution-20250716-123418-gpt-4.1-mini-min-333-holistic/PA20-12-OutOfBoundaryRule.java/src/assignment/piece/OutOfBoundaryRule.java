
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		var board = game.getBoard();
		int height = board.length;
		int width = board[0].length;

		var source = move.getSource();
		var destination = move.getDestination();

		int sourceX = source.getX();
		int sourceY = source.getY();
		int destX = destination.getX();
		int destY = destination.getY();

		boolean sourceInBounds = sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height;
		boolean destInBounds = destX >= 0 && destX < width && destY >= 0 && destY < height;

		return sourceInBounds && destInBounds;
	}

	@Override
	public String getDescription() {
		return "place is out of boundary of gameboard";
	}
}