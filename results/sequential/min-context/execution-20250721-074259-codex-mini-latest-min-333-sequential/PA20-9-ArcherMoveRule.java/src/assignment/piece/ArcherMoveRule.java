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
		int srcRow = move.getSource().getRowIndex();
		int srcCol = move.getSource().getColumnIndex();
		int dstRow = move.getDestination().getRowIndex();
		int dstCol = move.getDestination().getColumnIndex();
		if (srcRow != dstRow && srcCol != dstCol) {
			return false;
		}
		int countBetween = 0;
		int rowStep = Integer.compare(dstRow, srcRow);
		int colStep = Integer.compare(dstCol, srcCol);
		int r = srcRow + rowStep;
		int c = srcCol + colStep;
		while (r != dstRow || c != dstCol) {
			if (game.getPiece(r, c) != null) {
				countBetween++;
			}
			r += rowStep;
			c += colStep;
		}
		boolean isCapture = game.getPiece(move.getDestination()) != null;
		if (!isCapture) {
			return countBetween == 0;
		} else {
			return countBetween == 1;
		}
	}
}
