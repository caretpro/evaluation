
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.model.Position;

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

		Position source = move.getSource();
		Position dest = move.getDestination();

		// Move must be strictly horizontal or vertical
		if (source.getX() != dest.getX() && source.getY() != dest.getY()) {
			return false;
		}

		int countBetween = 0;

		if (source.getX() == dest.getX()) {
			// vertical move
			int x = source.getX();
			int startY = Math.min(source.getY(), dest.getY()) + 1;
			int endY = Math.max(source.getY(), dest.getY());

			for (int y = startY; y < endY; y++) {
				if (game.getPiece(new Position(x, y)) != null) {
					countBetween++;
				}
			}
		} else {
			// horizontal move
			int y = source.getY();
			int startX = Math.min(source.getX(), dest.getX()) + 1;
			int endX = Math.max(source.getX(), dest.getX());

			for (int x = startX; x < endX; x++) {
				if (game.getPiece(new Position(x, y)) != null) {
					countBetween++;
				}
			}
		}

		Piece destPiece = game.getPiece(dest);

		if (destPiece == null) {
			// Move without capture: no pieces between source and destination
			return countBetween == 0;
		} else {
			// Capture move: exactly one piece between source and destination
			// Also, destination piece must be opponent's piece
			Piece sourcePiece = game.getPiece(source);
			if (sourcePiece == null) {
				return false;
			}
			if (sourcePiece.owner().equals(destPiece.owner())) {
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