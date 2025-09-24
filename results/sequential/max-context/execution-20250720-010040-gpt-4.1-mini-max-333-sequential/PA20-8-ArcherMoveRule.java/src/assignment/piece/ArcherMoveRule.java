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
		var source = move.getSource();
		var dest = move.getDestination();
		int sx = source.x();
		int sy = source.y();
		int dx = dest.x();
		int dy = dest.y();
		if (sx != dx && sy != dy) {
			return false;
		}
		int countBetween = 0;
		if (sx == dx) {
			int minY = Math.min(sy, dy);
			int maxY = Math.max(sy, dy);
			for (int y = minY + 1; y < maxY; y++) {
				if (game.getPiece(sx, y) != null) {
					countBetween++;
				}
			}
		} else {
			int minX = Math.min(sx, dx);
			int maxX = Math.max(sx, dx);
			for (int x = minX + 1; x < maxX; x++) {
				if (game.getPiece(x, sy) != null) {
					countBetween++;
				}
			}
		}
		var sourcePiece = game.getPiece(source);
		var destPiece = game.getPiece(dest);
		if (destPiece == null) {
			return countBetween == 0;
		} else {
			return countBetween == 1 && !destPiece.getPlayer().equals(sourcePiece.getPlayer());
		}
	}
}
