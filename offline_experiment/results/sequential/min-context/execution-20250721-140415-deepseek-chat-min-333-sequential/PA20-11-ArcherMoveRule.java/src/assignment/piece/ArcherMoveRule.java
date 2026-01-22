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
		int srcX = move.getSource().x();
		int srcY = move.getSource().y();
		int destX = move.getDestination().x();
		int destY = move.getDestination().y();
		if (srcX != destX && srcY != destY) {
			return false;
		}
		boolean isCapture = game.getPiece(move.getDestination()) != null;
		int dx = Integer.signum(destX - srcX);
		int dy = Integer.signum(destY - srcY);
		int count = 0;
		int x = srcX + dx;
		int y = srcY + dy;
		while (x != destX || y != destY) {
			Place current = new Place(x, y);
			if (game.getPiece(current) != null) {
				count++;
			}
			x += dx;
			y += dy;
		}
		if (isCapture) {
			return count == 1;
		} else {
			return count == 0;
		}
	}
}
