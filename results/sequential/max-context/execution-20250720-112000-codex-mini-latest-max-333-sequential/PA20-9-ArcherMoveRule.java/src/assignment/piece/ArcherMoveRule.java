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
		int dx = dst.x() - src.x(), dy = dst.y() - src.y();
		if (dx != 0 && dy != 0) {
			return false;
		}
		int stepX = Integer.signum(dx), stepY = Integer.signum(dy);
		int screens = 0;
		for (int x = src.x() + stepX, y = src.y() + stepY; x != dst.x() || y != dst.y(); x += stepX, y += stepY) {
			if (game.getPiece(new assignment.protocol.Place(x, y)) != null) {
				screens++;
			}
		}
		var target = game.getPiece(dst);
		if (target == null) {
			return screens == 0;
		}
		return screens == 1 && target.getPlayer() != game.getPiece(src).getPlayer();
	}
}
