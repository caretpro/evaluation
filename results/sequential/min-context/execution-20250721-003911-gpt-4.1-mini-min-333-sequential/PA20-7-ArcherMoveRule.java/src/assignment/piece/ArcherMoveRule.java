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
		assignment.protocol.Place source = move.getSource();
		assignment.protocol.Place destination = move.getDestination();
		int srcRow = source.row();
		int srcCol = source.col();
		int destRow = destination.row();
		int destCol = destination.col();
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
		var destPiece = game.getPiece(destination);
		if (destPiece == null) {
			return countBetween == 0;
		} else {
			var srcPiece = game.getPiece(source);
			if (srcPiece.getPlayer().equals(destPiece.getPlayer())) {
				return false;
			}
			return countBetween == 1;
		}
	}
}
