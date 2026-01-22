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
		var piece = game.getPiece(source);
		var destPiece = game.getPiece(dest);
		if (source.x() != dest.x() && source.y() != dest.y()) {
			return false;
		}
		int countBetween = 0;
		if (source.x() == dest.x()) {
			int x = source.x();
			int startY = Math.min(source.y(), dest.y()) + 1;
			int endY = Math.max(source.y(), dest.y());
			for (int y = startY; y < endY; y++) {
				if (game.getPiece(x, y) != null) {
					countBetween++;
				}
			}
		} else {
			int y = source.y();
			int startX = Math.min(source.x(), dest.x()) + 1;
			int endX = Math.max(source.x(), dest.x());
			for (int x = startX; x < endX; x++) {
				if (game.getPiece(x, y) != null) {
					countBetween++;
				}
			}
		}
		if (destPiece == null) {
			return countBetween == 0;
		} else {
			if (destPiece.getPlayer().equals(piece.getPlayer())) {
				return false;
			}
			return countBetween == 1;
		}
	}
}
