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
		assignment.protocol.Place target = move.getTarget();
		int sourceRow = source.getRow();
		int sourceCol = source.getCol();
		int targetRow = target.getRow();
		int targetCol = target.getCol();
		if (sourceRow != targetRow && sourceCol != targetCol) {
			return false;
		}
		int piecesBetween = 0;
		if (sourceRow == targetRow) {
			int start = Math.min(sourceCol, targetCol);
			int end = Math.max(sourceCol, targetCol);
			for (int col = start + 1; col < end; col++) {
				assignment.protocol.Position position = new assignment.protocol.Position(sourceRow, col);
				if (game.getPiece(position) != null) {
					piecesBetween++;
				}
			}
		} else {
			int start = Math.min(sourceRow, targetRow);
			int end = Math.max(sourceRow, targetRow);
			for (int row = start + 1; row < end; row++) {
				assignment.protocol.Position position = new assignment.protocol.Position(row, sourceCol);
				if (game.getPiece(position) != null) {
					piecesBetween++;
				}
			}
		}
		if (game.getPiece(target) == null) {
			return piecesBetween == 0;
		} else {
			return piecesBetween == 1;
		}
	}
}
