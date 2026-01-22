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
		int sourceX = move.getSource().x();
		int sourceY = move.getSource().y();
		int destX = move.getDestination().x();
		int destY = move.getDestination().y();
		if (sourceX == destX) {
			int count = 0;
			int start = Math.min(sourceY, destY) + 1;
			int end = Math.max(sourceY, destY);
			for (int i = start; i < end; i++) {
				if (game.getPiece(sourceX, i) != null) {
					count++;
				}
			}
			if (game.getPiece(move.getDestination()) == null && count == 0)
				return true;
			else
				return game.getPiece(move.getDestination()) != null && count == 1;
		} else if (sourceY == destY) {
			int count = 0;
			int start = Math.min(sourceX, destX) + 1;
			int end = Math.max(sourceX, destX);
			for (int i = start; i < end; i++) {
				if (game.getPiece(i, sourceY) != null) {
					count++;
				}
			}
			if (game.getPiece(move.getDestination()) == null && count == 0)
				return true;
			else
				return game.getPiece(move.getDestination()) != null && count == 1;
		}
		return false;
	}
}
