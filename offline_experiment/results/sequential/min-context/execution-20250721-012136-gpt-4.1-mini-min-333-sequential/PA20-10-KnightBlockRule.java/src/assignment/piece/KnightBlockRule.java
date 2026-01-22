package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Place;

/**
 * The blocking rule applying on Knights. The rule is similar to the blocking rule for horse in Chinese chess.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Horse'>Wikipedia</a>
 */
public class KnightBlockRule implements Rule {
	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}

	@Override
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Knight)) {
			return true;
		}
		Place source = move.getSource();
		Place destination = move.getDestination();
		int rowDiff = destination.row() - source.row();
		int colDiff = destination.col() - source.col();
		if (!((Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1)
				|| (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2))) {
			return false;
		}
		Place leg;
		if (Math.abs(rowDiff) == 2) {
			leg = new Place(source.row() + rowDiff / 2, source.col());
		} else {
			leg = new Place(source.row(), source.col() + colDiff / 2);
		}
		if (game.getPiece(leg) != null) {
			return false;
		}
		return true;
	}
}
