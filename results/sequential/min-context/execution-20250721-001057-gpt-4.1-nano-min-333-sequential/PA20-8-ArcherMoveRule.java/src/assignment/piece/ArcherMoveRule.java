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
		int srcX = move.getSource().getX();
		int srcY = move.getSource().getY();
		int destX = move.getDestination().getX();
		int destY = move.getDestination().getY();
		if (srcX != destX && srcY != destY) {
			return false;
		}
		int countPiecesBetween = 0;
		if (srcX == destX) {
			int startY = Math.min(srcY, destY) + 1;
			int endY = Math.max(srcY, destY);
			for (int y = startY; y < endY; y++) {
				if (game.getPiece(new Place(srcX, y)) != null) {
					countPiecesBetween++;
				}
			}
		} else {
			int startX = Math.min(srcX, destX) + 1;
			int endX = Math.max(srcX, destX);
			for (int x = startX; x < endX; x++) {
				if (game.getPiece(new Place(x, srcY)) != null) {
					countPiecesBetween++;
				}
			}
		}
		boolean isCapture = game.getPiece(move.getDestination()) != null;
		if (isCapture) {
			return countPiecesBetween == 1;
		} else {
			return countPiecesBetween == 0;
		}
	}
}
