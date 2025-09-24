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
		Place target = move.getTarget();
		int sourceRow = source.getRow();
		int sourceColumn = source.getColumn();
		int targetRow = target.getRow();
		int targetColumn = target.getColumn();
		int rowDiff = Math.abs(targetRow - sourceRow);
		int colDiff = Math.abs(targetColumn - sourceColumn);
		if (rowDiff == 2 && colDiff == 1) {
			int blockingRow = (sourceRow + targetRow) / 2;
			Place blockingPlace = new Place(blockingRow, sourceColumn);
			return game.getPiece(blockingPlace) == null;
		} else if (rowDiff == 1 && colDiff == 2) {
			int blockingColumn = (sourceColumn + targetColumn) / 2;
			Place blockingPlace = new Place(sourceRow, blockingColumn);
			return game.getPiece(blockingPlace) == null;
		}
		return true;
	}
}
