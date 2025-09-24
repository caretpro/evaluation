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
		int boardSize = game.getBoardSize();
		assignment.protocol.Place source = move.getSource();
		assignment.protocol.Place destination = move.getDestination();
		int sourceRow = source.row();
		int sourceCol = source.col();
		int destRow = destination.row();
		int destCol = destination.col();
		if (sourceRow < 0 || sourceRow >= boardSize || sourceCol < 0 || sourceCol >= boardSize || destRow < 0
				|| destRow >= boardSize || destCol < 0 || destCol >= boardSize) {
			return false;
		}
		return true;
	}
}
