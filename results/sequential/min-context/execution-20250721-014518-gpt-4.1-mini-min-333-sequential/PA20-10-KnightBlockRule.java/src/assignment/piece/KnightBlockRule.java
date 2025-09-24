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
		int rowDiff = destination.getRowIndex() - source.getRowIndex();
		int colDiff = destination.getColumnIndex() - source.getColumnIndex();
		Place blockingPlace;
		if (Math.abs(rowDiff) == 2 && Math.abs(colDiff) == 1) {
			blockingPlace = new Place(source.getRowIndex() + rowDiff / 2, source.getColumnIndex());
		} else if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 2) {
			blockingPlace = new Place(source.getRowIndex(), source.getColumnIndex() + colDiff / 2);
		} else {
			return true;
		}
		return game.getPiece(blockingPlace) == null;
	}
}
