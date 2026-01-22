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
		int boardSize = game.boardSize();
		int sourceX = move.source().x();
		int sourceY = move.source().y();
		int destX = move.destination().x();
		int destY = move.destination().y();
		return sourceX >= 0 && sourceX < boardSize && sourceY >= 0 && sourceY < boardSize && destX >= 0
				&& destX < boardSize && destY >= 0 && destY < boardSize;
	}
}
