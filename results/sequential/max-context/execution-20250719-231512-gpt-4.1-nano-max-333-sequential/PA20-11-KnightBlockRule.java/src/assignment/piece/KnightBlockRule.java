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
		int srcX = move.getSource().x();
		int srcY = move.getSource().y();
		int destX = move.getDestination().x();
		int destY = move.getDestination().y();
		int deltaX = destX - srcX;
		int deltaY = destY - srcY;
		int legX = srcX;
		int legY = srcY;
		if (Math.abs(deltaX) == 2 && Math.abs(deltaY) == 1) {
			legX = srcX + (deltaX / 2);
			legY = srcY;
		} else if (Math.abs(deltaY) == 2 && Math.abs(deltaX) == 1) {
			legX = srcX;
			legY = srcY + (deltaY / 2);
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
