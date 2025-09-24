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
		Object sourcePiece = game.getPiece(move.getSource());
		Object targetPiece = game.getPiece(move.getDestination());
		if (!(sourcePiece instanceof Archer)) {
			return true;
		}
		int srcRow = move.getSource().getRow();
		int srcCol = move.getSource().getCol();
		int destRow = move.getDestination().getRow();
		int destCol = move.getDestination().getCol();
		if (srcRow != destRow && srcCol != destCol) {
			return false;
		}
		int countPiecesBetween = 0;
		if (srcRow == destRow) {
			int startCol = Math.min(srcCol, destCol) + 1;
			int endCol = Math.max(srcCol, destCol);
			for (int col = startCol; col < endCol; col++) {
				Place position = new Place(srcRow, col);
				if (game.getPiece(position) != null) {
					countPiecesBetween++;
				}
			}
		} else {
			int startRow = Math.min(srcRow, destRow) + 1;
			int endRow = Math.max(srcRow, destRow);
			for (int row = startRow; row < endRow; row++) {
				Place position = new Place(row, srcCol);
				if (game.getPiece(position) != null) {
					countPiecesBetween++;
				}
			}
		}
		boolean isCapture = targetPiece != null;
		if (isCapture) {
			return countPiecesBetween == 1;
		} else {
			return countPiecesBetween == 0;
		}
	}
}
