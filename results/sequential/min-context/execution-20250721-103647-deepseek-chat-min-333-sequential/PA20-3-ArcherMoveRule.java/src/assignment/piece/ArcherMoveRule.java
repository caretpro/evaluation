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
		int dx = move.getDestination().x() - move.getSource().x();
		int dy = move.getDestination().y() - move.getSource().y();
		if (dx != 0 && dy != 0) {
			return false;
		}
		int stepX = Integer.compare(dx, 0);
		int stepY = Integer.compare(dy, 0);
		int x = move.getSource().x() + stepX;
		int y = move.getSource().y() + stepY;
		int piecesCount = 0;
		while (x != move.getDestination().x() || y != move.getDestination().y()) {
			if (game.getPiece(new Move.Place(x, y)) != null) {
				piecesCount++;
			}
			x += stepX;
			y += stepY;
		}
		if (game.getPiece(move.getDestination()) != null) {
			return piecesCount == 1;
		} else {
			return piecesCount == 0;
		}
	}
}
