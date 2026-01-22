
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
		Piece sourcePiece = game.getPiece(move.getSource());
		if (!(sourcePiece instanceof Archer)) {
			return true;
		}

		Place source = move.getSource();
		Place dest = move.getDestination();

		// Archer moves only in straight lines (horizontal or vertical)
		if (source.getRow() != dest.getRow() && source.getColumn() != dest.getColumn()) {
			return false;
		}

		// Count pieces between source and destination
		int countBetween = countPiecesBetween(game, source, dest);
		if (countBetween < 0) {
			// Invalid count, treat as invalid move
			return false;
		}

		Piece destPiece = game.getPiece(dest);

		if (destPiece == null) {
			// Normal move: no pieces in between allowed
			return countBetween == 0;
		} else {
			// Capture move: must have exactly one piece between source and destination
			return countBetween == 1;
		}
	}

	@Override
	public String getDescription() {
		return "archer move rule is violated";
	}

	/**
	 * Counts the number of pieces between source and destination positions (exclusive).
	 */
	private int countPiecesBetween(Game game, Place source, Place dest) {
		int count = 0;

		if (source.getRow() == dest.getRow()) {
			// Horizontal move
			int row = source.getRow();
			int startCol = Math.min(source.getColumn(), dest.getColumn()) + 1;
			int endCol = Math.max(source.getColumn(), dest.getColumn());
			for (int col = startCol; col < endCol; col++) {
				Place intermediate = new Place(row, col);
				if (game.getPiece(intermediate) != null) {
					count++;
				}
			}
		} else if (source.getColumn() == dest.getColumn()) {
			// Vertical move
			int col = source.getColumn();
			int startRow = Math.min(source.getRow(), dest.getRow()) + 1;
			int endRow = Math.max(source.getRow(), dest.getRow());
			for (int row = startRow; row < endRow; row++) {
				Place intermediate = new Place(row, col);
				if (game.getPiece(intermediate) != null) {
					count++;
				}
			}
		} else {
			// Not a straight line move
			return -1;
		}

		return count;
	}
}