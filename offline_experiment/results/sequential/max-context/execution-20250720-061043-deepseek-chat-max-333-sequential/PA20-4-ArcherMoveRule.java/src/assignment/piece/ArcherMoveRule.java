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
		assignment.protocol.Place dest = move.getDestination();
		if (source.x() != dest.x() && source.y() != dest.y()) {
			return false;
		}
		int dx = Integer.compare(dest.x(), source.x());
		int dy = Integer.compare(dest.y(), source.y());
		int piecesBetween = 0;
		assignment.protocol.Place current = new assignment.protocol.Place(source.x() + dx, source.y() + dy);
		while (!current.equals(dest)) {
			if (game.getPiece(current) != null) {
				piecesBetween++;
			}
			current = new assignment.protocol.Place(current.x() + dx, current.y() + dy);
		}
		assignment.protocol.Piece targetPiece = game.getPiece(dest);
		if (targetPiece == null) {
			return piecesBetween == 0;
		} else {
			return piecesBetween == 1 && targetPiece.getPlayer() != game.getPiece(source).getPlayer();
		}
	}
}
