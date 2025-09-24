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
		int dx = destination.getFile() - source.getFile();
		int dy = destination.getRank() - source.getRank();
		Place leg;
		if (Math.abs(dx) == 2 && Math.abs(dy) == 1) {
			leg = new Place(source.getFile() + dx / 2, source.getRank());
		} else if (Math.abs(dx) == 1 && Math.abs(dy) == 2) {
			leg = new Place(source.getFile(), source.getRank() + dy / 2);
		} else {
			return true;
		}
		return game.getPiece(leg) == null;
	}
}
