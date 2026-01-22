
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * Global rule that requires the source and destination should be inside the board boundary.
 */
public class OutOfBoundaryRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		int boardSize = game.getBoardSize();
		Place destination = move.getDestination();
		Place source = move.getSource();
		return destination.getRow() >= 0 && destination.getRow() < boardSize
			&& destination.getCol() >= 0 && destination.getCol() < boardSize
			&& source.getRow() >= 0 && source.getRow() < boardSize
			&& source.getCol() >= 0 && source.getCol() < boardSize;
	}

	@Override
	public String getDescription() {
		return "place is out of boundary of gameboard";
	}
}