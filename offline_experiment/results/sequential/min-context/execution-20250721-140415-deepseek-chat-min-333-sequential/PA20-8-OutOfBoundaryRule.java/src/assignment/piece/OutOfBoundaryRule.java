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
		return move.source().row() >= 0 && move.source().row() < boardSize && move.source().column() >= 0
				&& move.source().column() < boardSize && move.destination().row() >= 0
				&& move.destination().row() < boardSize && move.destination().column() >= 0
				&& move.destination().column() < boardSize;
	}
}
