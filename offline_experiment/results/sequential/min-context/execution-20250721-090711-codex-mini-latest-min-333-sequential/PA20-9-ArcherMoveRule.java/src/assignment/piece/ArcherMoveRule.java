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
		if (!(game.pieceAt(move.src()) instanceof Archer)) {
			return true;
		}
		int sr = move.src().row(), sc = move.src().col();
		int dr = move.dst().row(), dc = move.dst().col();
		if (sr != dr && sc != dc) {
			return false;
		}
		int screens = 0;
		if (sr == dr) {
			int dir = (dc > sc ? 1 : -1);
			for (int c = sc + dir; c != dc; c += dir) {
				if (game.pieceAt(Place.at(sr, c)) != null) {
					screens++;
				}
			}
		} else {
			int dir = (dr > sr ? 1 : -1);
			for (int r = sr + dir; r != dr; r += dir) {
				if (game.pieceAt(Place.at(r, sc)) != null) {
					screens++;
				}
			}
		}
		if (game.pieceAt(move.dst()) == null) {
			return screens == 0;
		}
		return screens == 1 && game.pieceAt(move.dst()).owner() != game.pieceAt(move.src()).owner();
	}
}
