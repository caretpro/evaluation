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
		int srcRow = move.getSource().row;
		int srcCol = move.getSource().col;
		int dstRow = move.getDestination().row;
		int dstCol = move.getDestination().col;
		if (srcRow != dstRow && srcCol != dstCol) {
			return false;
		}
		int countBetween = 0;
		if (srcRow == dstRow) {
			int start = Math.min(srcCol, dstCol) + 1;
			int end = Math.max(srcCol, dstCol);
			for (int c = start; c < end; c++) {
				if (game.getPiece(new assignment.protocol.Place(srcRow, c)) != null) {
					countBetween++;
				}
			}
		} else {
			int start = Math.min(srcRow, dstRow) + 1;
			int end = Math.max(srcRow, dstRow);
			for (int r = start; r < end; r++) {
				if (game.getPiece(new assignment.protocol.Place(r, srcCol)) != null) {
					countBetween++;
				}
			}
		}
		var destPiece = game.getPiece(move.getDestination());
		if (destPiece == null) {
			return countBetween == 0;
		} else {
			var srcPiece = game.getPiece(move.getSource());
			if (srcPiece.owner().equals(destPiece.owner())) {
				return false;
			}
			return countBetween == 1;
		}
	}
}
