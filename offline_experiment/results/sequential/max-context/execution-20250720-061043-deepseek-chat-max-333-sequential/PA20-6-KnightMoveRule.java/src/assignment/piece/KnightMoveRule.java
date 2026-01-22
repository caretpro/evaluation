package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * Moving rule of Knight in chess (no block)
 */
public class KnightMoveRule implements Rule {

	@Override
	public String getDescription() {
		return "knight move rule is violated";
	}

	@Override
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Knight)) {
			return true;
		}
		assignment.protocol.Place source = move.getSource();
		assignment.protocol.Place destination = move.getDestination();
		int dx = Math.abs(destination.x() - source.x());
		int dy = Math.abs(destination.y() - source.y());
		return (dx == 2 && dy == 1) || (dx == 1 && dy == 2);
	}
}
