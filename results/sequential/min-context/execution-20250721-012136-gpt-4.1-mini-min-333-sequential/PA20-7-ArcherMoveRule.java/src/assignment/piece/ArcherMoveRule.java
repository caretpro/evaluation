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
		int srcRow = source.getRow();
		int srcCol = source.getCol();
		int destRow = dest.getRow();
		int destCol = dest.getCol();
		if (srcRow != destRow && srcCol != destCol) {
			return false;
		}
		int countBetween = 0;
		if (srcRow == destRow) {
			int start = Math.min(srcCol, destCol) + 1;
			int end = Math.max(srcCol, destCol);
			for (int c = start; c < end; c++) {
				if (game.getPiece(new assignment.protocol.Place(srcRow, c)) != null) {
					countBetween++;
				}
			}
		} else {
			int start = Math.min(srcRow, destRow) + 1;
			int end = Math.max(srcRow, destRow);
			for (int r = start; r < end; r++) {
				if (game.getPiece(new assignment.protocol.Place(r, srcCol)) != null) {
					countBetween++;
				}
			}
		}
		var destPiece = game.getPiece(dest);
		if (destPiece == null) {
			return countBetween == 0;
		} else {
			var sourcePiece = game.getPiece(source);
			if (sourcePiece.owner().equals(destPiece.owner())) {
				return false;
			}
			return countBetween == 1;
		}
	}
}
