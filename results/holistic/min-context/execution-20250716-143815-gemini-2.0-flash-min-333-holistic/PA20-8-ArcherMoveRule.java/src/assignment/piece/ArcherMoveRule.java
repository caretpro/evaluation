
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The rule of moving of Archer, which is similar to the moving rule of cannon in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class ArcherMoveRule implements Rule {
	@Override
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Archer)) {
			return true;
		}

		Place source = move.getSource();
		Place destination = move.getDestination();

		int srcRow = source.getRow();
		int srcCol = source.getColumn();
		int destRow = destination.getRow();
		int destCol = destination.getColumn();

		// Calculate the number of pieces between the source and destination
		int piecesBetween = 0;

		if (srcRow == destRow) { // Horizontal move
			int startCol = Math.min(srcCol, destCol) + 1;
			int endCol = Math.max(srcCol, destCol);
			for (int col = startCol; col < endCol; col++) {
				if (game.getPiece(srcRow, col) != null) {
					piecesBetween++;
				}
			}
		} else if (srcCol == destCol) { // Vertical move
			int startRow = Math.min(srcRow, destRow) + 1;
			int endRow = Math.max(srcRow, destRow);
			for (int row = startRow; row < endRow; row++) {
				if (game.getPiece(row, srcCol) != null) {
					piecesBetween++;
				}
			}
		} else {
			return false; // Invalid move: Archer must move horizontally or vertically
		}

		// Validate the move based on the number of pieces between
		if (game.getPiece(destination) == null) {
			// If the destination is empty, there should be no pieces between
			return piecesBetween == 0;
		} else {
			// If the destination is not empty, there should be exactly one piece between
			return piecesBetween == 1;
		}
	}

	@Override
	public String getDescription() {
		return "archer move rule is violated";
	}
}