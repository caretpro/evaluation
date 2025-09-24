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
		Piece sourcePiece = game.getPiece(move.getSource());
		if (!(sourcePiece instanceof Archer)) {
			return true;
		}
		int srcX = move.getSource().x();
		int srcY = move.getSource().y();
		int destX = move.getDestination().x();
		int destY = move.getDestination().y();
		if (srcX != destX && srcY != destY) {
			return false;
		}
		int countPiecesInBetween = 0;
		if (srcX == destX) {
			int step = destY > srcY ? 1 : -1;
			for (int y = srcY + step; y != destY; y += step) {
				if (game.getPiece(srcX, y) != null) {
					countPiecesInBetween++;
				}
			}
		} else {
			int step = destX > srcX ? 1 : -1;
			for (int x = srcX + step; x != destX; x += step) {
				if (game.getPiece(x, srcY) != null) {
					countPiecesInBetween++;
				}
			}
		}
		Piece destPiece = game.getPiece(move.getDestination());
		if (destPiece == null) {
			return countPiecesInBetween == 0;
		} else {
			if (destPiece.getPlayer().equals(sourcePiece.getPlayer())) {
				return false;
			}
			return countPiecesInBetween == 1;
		}
	}
}
