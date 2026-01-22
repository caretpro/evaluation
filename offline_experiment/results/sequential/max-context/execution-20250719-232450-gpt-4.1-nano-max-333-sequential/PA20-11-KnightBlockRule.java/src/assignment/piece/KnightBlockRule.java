package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The blocking rule applying on Knights. The rule is similar to the blocking rule for horse in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Horse'>Wikipedia</a>
 */
public class KnightBlockRule implements Rule {
	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}

	@Override
	public boolean validate(Game game, Move move) {
		Piece sourcePiece = game.getPiece(move.getSource());
		if (!(sourcePiece instanceof Knight)) {
			return true;
		}
		int x1 = move.getSource().x();
		int y1 = move.getSource().y();
		int x2 = move.getDestination().x();
		int y2 = move.getDestination().y();
		int dx = x2 - x1;
		int dy = y2 - y1;
		int legX = x1;
		int legY = y1;
		if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
			legX = x1 + (dx / 2);
			legY = y1;
		} else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
			legX = x1;
			legY = y1 + (dy / 2);
		} else {
			return true;
		}
		Piece blockingPiece = game.getPiece(legX, legY);
		if (blockingPiece != null) {
			return false;
		}
		return true;
	}
}
