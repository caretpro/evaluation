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
		if (!(game.getPiece(move.source()) instanceof Archer)) {
			return true;
		}
		if (move.source().x() != move.target().x() && move.source().y() != move.target().y()) {
			return false;
		}
		int pieceCount = game.countPiecesBetween(move.source(), move.target());
		Object targetPiece = game.getPiece(move.target());
		if (targetPiece != null) {
			return pieceCount == 1;
		} else {
			return pieceCount == 0;
		}
	}
}
