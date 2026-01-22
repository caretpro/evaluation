
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
	public boolean validate(Game game, Move move) {
		if (!(game.getPiece(move.getSource()) instanceof Knight)) {
			return true;
		}

		Place sourcePlace = move.getSource();
		Place destinationPlace = move.getDestination();

		int sourceRow = sourcePlace.getRow();
		int sourceCol = sourcePlace.getCol();
		int targetRow = destinationPlace.getRow();
		int targetCol = destinationPlace.getCol();

		int rowDiff = Math.abs(targetRow - sourceRow);
		int colDiff = Math.abs(targetCol - sourceCol);

		if (rowDiff == 2 && colDiff == 1) {
			// Vertical move of 2, horizontal move of 1
			int blockRow = (sourceRow + targetRow) / 2;
			Place blockPlace = new Place(blockRow, sourceCol);
			return game.getPiece(blockPlace) == null;
		} else if (rowDiff == 1 && colDiff == 2) {
			// Vertical move of 1, horizontal move of 2
			int blockCol = (sourceCol + targetCol) / 2;
			Place blockPlace = new Place(sourceRow, blockCol);
			return game.getPiece(blockPlace) == null;
		}

		return false;
	}

	@Override
	public String getDescription() {
		return "knight is blocked by another piece";
	}
}