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
		var size = game.getSize();
		int width = size.x();
		int height = size.y();
		var source = move.getSource();
		var dest = move.getDestination();
		int sourceX = source.x();
		int sourceY = source.y();
		int destX = dest.x();
		int destY = dest.y();
		boolean sourceInBounds = sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height;
		boolean destInBounds = destX >= 0 && destX < width && destY >= 0 && destY < height;
		return sourceInBounds && destInBounds;
	}
}
