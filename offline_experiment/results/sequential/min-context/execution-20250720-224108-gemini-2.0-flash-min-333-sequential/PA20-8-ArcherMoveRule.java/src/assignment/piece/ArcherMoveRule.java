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
		Place source = move.getSource();
		Place target = move.getTarget();
		int sourceRow = source.getRow();
		int sourceCol = source.getCol();
		int targetRow = target.getRow();
		int targetCol = target.getCol();
		int rowDiff = Math.abs(sourceRow - targetRow);
		int colDiff = Math.abs(sourceCol - targetCol);
		if ((rowDiff > 0 && colDiff > 0) || (rowDiff == 0 && colDiff == 0)) {
			return false;
		}
		Piece targetPiece = game.getPiece(target);
		boolean isCapture = targetPiece != null;
		int piecesBetween = 0;
		if (sourceRow == targetRow) {
			int start = Math.min(sourceCol, targetCol) + 1;
			int end = Math.max(sourceCol, targetCol);
			for (int col = start; col < end; col++) {
				if (game.getPiece(sourceRow, col) != null) {
					piecesBetween++;
				}
			}
		} else {
			int start = Math.min(sourceRow, targetRow) + 1;
			int end = Math.max(sourceRow, targetRow);
			for (int row = start; row < end; row++) {
				if (game.getPiece(row, sourceCol) != null) {
					piecesBetween++;
				}
			}
		}
		if (isCapture) {
			return piecesBetween == 1;
		} else {
			return piecesBetween == 0;
		}
	}
}
