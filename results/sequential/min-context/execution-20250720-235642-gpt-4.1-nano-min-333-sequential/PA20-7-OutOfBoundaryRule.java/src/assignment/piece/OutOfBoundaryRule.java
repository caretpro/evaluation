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
		java.awt.Dimension size = game.getBoardSize();
		int width = size.width;
		int height = size.height;
		int sourceX = move.getSource().getX();
		int sourceY = move.getSource().getY();
		int destX = move.getDestination().getX();
		int destY = move.getDestination().getY();
		boolean sourceInBounds = sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height;
		boolean destInBounds = destX >= 0 && destX < width && destY >= 0 && destY < height;
		return sourceInBounds && destInBounds;
	}
}
