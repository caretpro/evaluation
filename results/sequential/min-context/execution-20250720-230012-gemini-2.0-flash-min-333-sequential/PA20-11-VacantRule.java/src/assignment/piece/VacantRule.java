package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Global rule that requires the source place of a move must have a piece on it.
 */
public class VacantRule implements Rule {
	@Override
	public String getDescription() {
		return "the source of move should have a piece";
	}

	@Override
	public boolean validate(Game game, Move move) {
		if (game == null || move == null) {
			return false;
		}
		Place source = move.getSource();
		if (game.getPiece(source) == null) {
			return false;
		}
		return true;
	}
}
