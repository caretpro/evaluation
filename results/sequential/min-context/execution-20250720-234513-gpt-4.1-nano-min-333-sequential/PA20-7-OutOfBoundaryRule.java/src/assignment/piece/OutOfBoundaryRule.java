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
		int width = game.getBoardWidth();
		int height = game.getBoardHeight();
		int sourceX = move.getSource().getColumn();
		int sourceY = move.getSource().getRow();
		int destX = move.getDestination().getColumn();
		int destY = move.getDestination().getRow();
		boolean sourceInBounds = sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height;
		boolean destInBounds = destX >= 0 && destX < width && destY >= 0 && destY < height;
		return sourceInBounds && destInBounds;
	}
}
