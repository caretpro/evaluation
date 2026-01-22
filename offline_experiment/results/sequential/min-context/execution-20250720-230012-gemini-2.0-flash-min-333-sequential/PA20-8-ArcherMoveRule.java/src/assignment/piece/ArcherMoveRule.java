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
		int sourceRow = move.getSource().x();
		int sourceCol = move.getSource().y();
		int targetRow = move.getDestination().x();
		int targetCol = move.getDestination().y();
		if (sourceRow != targetRow && sourceCol != targetCol) {
			return false;
		}
		int piecesBetween = 0;
		if (sourceRow == targetRow) {
			int start = Math.min(sourceCol, targetCol);
			int end = Math.max(sourceCol, targetCol);
			for (int col = start + 1; col < end; col++) {
				if (game.getPiece(move.getSource().copy(sourceRow, col)) != null) {
					piecesBetween++;
				}
			}
		} else {
			int start = Math.min(sourceRow, targetRow);
			int end = Math.max(sourceRow, targetRow);
			for (int row = start + 1; row < end; row++) {
				if (game.getPiece(move.getSource().copy(row, sourceCol)) != null) {
					piecesBetween++;
				}
			}
		}
		if (game.getPiece(move.getDestination()) == null) {
			return piecesBetween == 0;
		} else {
			return piecesBetween == 1;
		}
	}
}
