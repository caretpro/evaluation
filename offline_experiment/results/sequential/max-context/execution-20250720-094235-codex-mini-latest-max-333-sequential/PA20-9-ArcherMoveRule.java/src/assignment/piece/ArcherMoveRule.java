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
		int sx = src.x(), sy = src.y();
		int dx = dst.x(), dy = dst.y();
		if (sx != dx && sy != dy) {
			return false;
		}
		int dxStep = Integer.compare(dx, sx);
		int dyStep = Integer.compare(dy, sy);
		int screens = 0;
		int x = sx + dxStep, y = sy + dyStep;
		while (x != dx || y != dy) {
			if (game.getPiece(x, y) != null) {
				screens++;
			}
			x += dxStep;
			y += dyStep;
		}
		if (game.getPiece(dst) == null) {
			return screens == 0;
		}
		return screens == 1;
	}
}
