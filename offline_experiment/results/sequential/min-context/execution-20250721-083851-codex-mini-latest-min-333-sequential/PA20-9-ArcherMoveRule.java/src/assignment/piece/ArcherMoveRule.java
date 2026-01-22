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
		int x1 = move.getSource().col();
		int y1 = move.getSource().row();
		int x2 = move.getDestination().col();
		int y2 = move.getDestination().row();
		if (x1 != x2 && y1 != y2) {
			return false;
		}
		int dx = Integer.compare(x2, x1);
		int dy = Integer.compare(y2, y1);
		int intervening = 0;
		int cx = x1 + dx, cy = y1 + dy;
		while (cx != x2 || cy != y2) {
			if (game.getPiece(Position.of(cy, cx)) != null) {
				intervening++;
			}
			cx += dx;
			cy += dy;
		}
		if (game.getPiece(move.getDestination()) == null) {
			return intervening == 0;
		} else {
			return intervening == 1;
		}
	}
}
