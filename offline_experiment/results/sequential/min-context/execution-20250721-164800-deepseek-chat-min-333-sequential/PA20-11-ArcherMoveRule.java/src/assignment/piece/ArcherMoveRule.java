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
		if (!(game.getPiece(move.from()) instanceof Archer)) {
			return true;
		}
		if (move.from().row() != move.to().row() && move.from().col() != move.to().col()) {
			return false;
		}
		int count = 0;
		int rowStep = Integer.signum(move.to().row() - move.from().row());
		int colStep = Integer.signum(move.to().col() - move.from().col());
		int currentRow = move.from().row() + rowStep;
		int currentCol = move.from().col() + colStep;
		while (currentRow != move.to().row() || currentCol != move.to().col()) {
			if (game.getPieceAt(currentRow, currentCol) != null) {
				count++;
			}
			currentRow += rowStep;
			currentCol += colStep;
		}
		if (game.getPiece(move.to()) != null) {
			return count == 1;
		} else {
			return count == 0;
		}
	}
}
