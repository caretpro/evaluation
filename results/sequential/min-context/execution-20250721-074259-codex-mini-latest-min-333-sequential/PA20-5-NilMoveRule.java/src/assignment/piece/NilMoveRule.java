package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source and destination of a move should not be the same.
 */
public class NilMoveRule implements Rule {
	@Override
	public String getDescription() {
		return "the source and destination of move should be different places";
	}

	@Override
	public boolean validate(Game game, Move move) {
		return !move.source().equals(move.destination());
	}
}
