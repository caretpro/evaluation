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
		var src = move.getSource();
		var dst = move.getDestination();
		int sx = src.x(), sy = src.y(), dx = dst.x(), dy = dst.y();
		if (sx != dx && sy != dy) {
			return false;
		}
		int count = 0;
		int stepX = Integer.compare(dx, sx);
		int stepY = Integer.compare(dy, sy);
		int x = sx + stepX, y = sy + stepY;
		while (x != dx || y != dy) {
			if (game.getPiece(x, y) != null) {
				count++;
			}
			x += stepX;
			y += stepY;
		}
		var destPiece = game.getPiece(dst);
		if (destPiece == null) {
			return count == 0;
		} else {
			return count == 1;
		}
	}
}
