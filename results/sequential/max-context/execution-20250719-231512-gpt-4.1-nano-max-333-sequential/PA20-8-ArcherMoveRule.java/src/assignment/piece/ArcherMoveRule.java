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
		Piece sourcePiece = game.getPiece(move.getSource());
		if (!(sourcePiece instanceof Archer)) {
			return true;
		}
		int srcX = move.getSource().x();
		int srcY = move.getSource().y();
		int destX = move.getDestination().x();
		int destY = move.getDestination().y();
		if (srcX != destX && srcY != destY) {
			return false;
		}
		int countBetween = 0;
		if (srcX == destX) {
			int startY = Math.min(srcY, destY) + 1;
			int endY = Math.max(srcY, destY);
			for (int y = startY; y < endY; y++) {
				if (game.getPiece(srcX, y) != null) {
					countBetween++;
				}
			}
		} else {
			int startX = Math.min(srcX, destX) + 1;
			int endX = Math.max(srcX, destX);
			for (int x = startX; x < endX; x++) {
				if (game.getPiece(x, srcY) != null) {
					countBetween++;
				}
			}
		}
		Piece destPiece = game.getPiece(move.getDestination());
		if (destPiece == null) {
			return countBetween == 0;
		} else {
			return countBetween == 1 && !destPiece.getPlayer().equals(game.getCurrentPlayer());
		}
	}
}
