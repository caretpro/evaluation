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
		var destination = move.getDestination();
		int srcX = source.x();
		int srcY = source.y();
		int destX = destination.x();
		int destY = destination.y();
		if (srcX != destX && srcY != destY) {
			return false;
		}
		int countBetween = 0;
		if (srcX == destX) {
			int minY = Math.min(srcY, destY);
			int maxY = Math.max(srcY, destY);
			for (int y = minY + 1; y < maxY; y++) {
				if (game.getPiece(srcX, y) != null) {
					countBetween++;
				}
			}
		} else {
			int minX = Math.min(srcX, destX);
			int maxX = Math.max(srcX, destX);
			for (int x = minX + 1; x < maxX; x++) {
				if (game.getPiece(x, srcY) != null) {
					countBetween++;
				}
			}
		}
		var destPiece = game.getPiece(destination);
		if (destPiece == null) {
			return countBetween == 0;
		} else {
			var sourcePiece = game.getPiece(source);
			if (destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
				return false;
			}
			return countBetween == 1;
		}
	}
}
