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
		assignment.protocol.Place source = move.getSource();
		assignment.protocol.Place target = move.getDestination();
		int sourceRow = source.getRow();
		int sourceCol = source.getCol();
		int targetRow = target.getRow();
		int targetCol = target.getCol();
		if (sourceRow != targetRow && sourceCol != targetCol) {
			return false;
		}
		int countPiecesBetween = 0;
		if (sourceRow == targetRow) {
			int startCol = Math.min(sourceCol, targetCol) + 1;
			int endCol = Math.max(sourceCol, targetCol);
			for (int col = startCol; col < endCol; col++) {
				assignment.protocol.Place position = new assignment.protocol.Place(sourceRow, col);
				if (game.getPiece(position) != null) {
					countPiecesBetween++;
				}
			}
		} else {
			int startRow = Math.min(sourceRow, targetRow) + 1;
			int endRow = Math.max(sourceRow, targetRow);
			for (int row = startRow; row < endRow; row++) {
				assignment.protocol.Place position = new assignment.protocol.Place(row, sourceCol);
				if (game.getPiece(position) != null) {
					countPiecesBetween++;
				}
			}
		}
		boolean isCapture = game.getPiece(target) != null;
		if (isCapture) {
			return countPiecesBetween == 1;
		} else {
			return countPiecesBetween == 0;
		}
	}
}
