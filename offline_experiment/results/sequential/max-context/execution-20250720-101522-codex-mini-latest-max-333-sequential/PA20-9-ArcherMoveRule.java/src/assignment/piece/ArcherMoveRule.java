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
		int x0 = src.x(), y0 = src.y();
		int x1 = dst.x(), y1 = dst.y();
		if (x0 != x1 && y0 != y1) {
			return false;
		}
		int between = 0;
		if (x0 == x1) {
			int minY = Math.min(y0, y1), maxY = Math.max(y0, y1);
			for (int y = minY + 1; y < maxY; y++) {
				if (game.getPiece(x0, y) != null) {
					between++;
				}
			}
		} else {
			int minX = Math.min(x0, x1), maxX = Math.max(x0, x1);
			for (int x = minX + 1; x < maxX; x++) {
				if (game.getPiece(x, y0) != null) {
					between++;
				}
			}
		}
		var target = game.getPiece(dst);
		if (target == null) {
			return between == 0;
		} else {
			var srcPiece = game.getPiece(src);
			if (srcPiece.getPlayer().equals(target.getPlayer())) {
				return false;
			}
			return between == 1;
		}
	}
}
