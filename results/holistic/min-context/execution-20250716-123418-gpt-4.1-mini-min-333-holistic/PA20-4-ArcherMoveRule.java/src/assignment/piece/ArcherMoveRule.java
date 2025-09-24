
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		Piece piece = game.getPiece(move.getSource());
		if (!(piece instanceof Archer)) {
			return true;
		}

		Place src = move.getSource();
		Place dst = move.getDestination();

		int srcRow = src.getRow();
		int srcCol = src.getCol();
		int dstRow = dst.getRow();
		int dstCol = dst.getCol();

		// Must move in a straight line (horizontal or vertical)
		if (srcRow != dstRow && srcCol != dstCol) {
			return false;
		}

		// Count pieces between source and destination
		int countBetween = 0;

		if (srcRow == dstRow) {
			// Horizontal move
			int start = Math.min(srcCol, dstCol) + 1;
			int end = Math.max(srcCol, dstCol);
			for (int c = start; c < end; c++) {
				if (game.getPiece(new Place(srcRow, c)) != null) {
					countBetween++;
				}
			}
		} else {
			// Vertical move
			int start = Math.min(srcRow, dstRow) + 1;
			int end = Math.max(srcRow, dstRow);
			for (int r = start; r < end; r++) {
				if (game.getPiece(new Place(r, srcCol)) != null) {
					countBetween++;
				}
			}
		}

		Piece destPiece = game.getPiece(dst);

		if (destPiece == null) {
			// Move without capture: path must be clear (no pieces in between)
			return countBetween == 0;
		} else {
			// Capture move: must jump exactly one piece (screen)
			if (game.getOwner(dst) == game.getOwner(src)) {
				// Cannot capture own piece
				return false;
			}
			return countBetween == 1;
		}
	}

	@Override
	public String getDescription() {
		return "archer move rule is violated";
	}
}