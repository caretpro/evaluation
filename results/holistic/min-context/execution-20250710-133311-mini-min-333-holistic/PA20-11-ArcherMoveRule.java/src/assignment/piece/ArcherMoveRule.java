
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

		Place source = move.getSource();
		Place dest = move.getDestination();

		// Archer moves only in straight lines (horizontal or vertical)
		if (source.getRow() != dest.getRow() && source.getColumn() != dest.getColumn()) {
			return false;
		}

		int r1 = source.getRow();
		int c1 = source.getColumn();
		int r2 = dest.getRow();
		int c2 = dest.getColumn();

		int countBetween = 0;

		// Count pieces between source and destination
		if (r1 == r2) {
			// horizontal move
			int minC = Math.min(c1, c2);
			int maxC = Math.max(c1, c2);
			for (int c = minC + 1; c < maxC; c++) {
				if (game.getPiece(new Place(r1, c)) != null) {
					countBetween++;
				}
			}
		} else {
			// vertical move
			int minR = Math.min(r1, r2);
			int maxR = Math.max(r1, r2);
			for (int r = minR + 1; r < maxR; r++) {
				if (game.getPiece(new Place(r, c1)) != null) {
					countBetween++;
				}
			}
		}

		Piece destPiece = game.getPiece(dest);

		if (destPiece == null) {
			// Moving without capture: no pieces between source and destination
			return countBetween == 0;
		} else {
			// Capturing move: exactly one piece between source and destination
			// and destination piece belongs to opponent
			return countBetween == 1 && !piece.getPlayer().equals(destPiece.getPlayer());
		}
	}

	@Override
	public String getDescription() {
		return "archer move rule is violated";
	}
}