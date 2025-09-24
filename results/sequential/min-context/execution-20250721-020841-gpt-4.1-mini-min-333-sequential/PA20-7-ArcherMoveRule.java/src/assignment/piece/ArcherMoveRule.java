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
		if (!(game.getPiece(move.source()) instanceof Archer)) {
			return true;
		}
		assignment.protocol.Place source = move.source();
		assignment.protocol.Place target = move.target();
		int srcRow = source.getRow();
		int srcCol = source.getColumn();
		int tgtRow = target.getRow();
		int tgtCol = target.getColumn();
		if (srcRow != tgtRow && srcCol != tgtCol) {
			return false;
		}
		int countBetween = 0;
		if (srcRow == tgtRow) {
			int start = Math.min(srcCol, tgtCol) + 1;
			int end = Math.max(srcCol, tgtCol);
			for (int c = start; c < end; c++) {
				if (game.getPiece(new assignment.protocol.Place(srcRow, c)) != null) {
					countBetween++;
				}
			}
		} else {
			int start = Math.min(srcRow, tgtRow) + 1;
			int end = Math.max(srcRow, tgtRow);
			for (int r = start; r < end; r++) {
				if (game.getPiece(new assignment.protocol.Place(r, srcCol)) != null) {
					countBetween++;
				}
			}
		}
		var targetPiece = game.getPiece(target);
		var sourcePiece = game.getPiece(source);
		if (targetPiece == null) {
			return countBetween == 0;
		} else {
			return countBetween == 1 && !targetPiece.getOwner().equals(sourcePiece.getOwner());
		}
	}
}
