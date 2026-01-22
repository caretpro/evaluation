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
		int sourceX = move.getSource().getX();
		int sourceY = move.getSource().getY();
		int targetX = move.getTarget().getX();
		int targetY = move.getTarget().getY();
		if (sourceX != targetX && sourceY != targetY) {
			return false;
		}
		int countPiecesBetween = 0;
		if (sourceX == targetX) {
			int startY = Math.min(sourceY, targetY) + 1;
			int endY = Math.max(sourceY, targetY);
			for (int y = startY; y < endY; y++) {
				if (game.getPiece(move.getSource().withY(y)) != null) {
					countPiecesBetween++;
				}
			}
		} else {
			int startX = Math.min(sourceX, targetX) + 1;
			int endX = Math.max(sourceX, targetX);
			for (int x = startX; x < endX; x++) {
				if (game.getPiece(move.getSource().withX(x)) != null) {
					countPiecesBetween++;
				}
			}
		}
		boolean isCapturing = game.getPiece(move.getTarget()) != null;
		if (isCapturing) {
			return countPiecesBetween == 1;
		} else {
			return countPiecesBetween == 0;
		}
	}
}
