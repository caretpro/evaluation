package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
	@Override
	public String getDescription() {
		return "archer move rule is violated";
	}

	@Override
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Archer)) {
			return true;
		}
		Location from = move.getSource();
		Location to = move.getDestination();
		int dx = Integer.compare(to.x(), from.x());
		int dy = Integer.compare(to.y(), from.y());
		if (dx != 0 && dy != 0) {
			return false;
		}
		int steps = Math.max(Math.abs(to.x() - from.x()), Math.abs(to.y() - from.y()));
		if (steps == 0) {
			return false;
		}
		int screenCount = 0;
		int cx = from.x(), cy = from.y();
		for (int i = 1; i < steps; i++) {
			cx += dx;
			cy += dy;
			if (game.getPiece(new Location(cx, cy)) != null) {
				screenCount++;
			}
		}
		var targetPiece = game.getPiece(to);
		if (targetPiece == null) {
			return screenCount == 0;
		} else {
			return screenCount == 1 && !targetPiece.getOwner().equals(game.getCurrentPlayer());
		}
	}
}
