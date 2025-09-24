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
		int countBetween = 0;
		int stepX = Integer.compare(destX, srcX);
		int stepY = Integer.compare(destY, srcY);
		int currentX = srcX + stepX;
		int currentY = srcY + stepY;
		while (currentX != destX || currentY != destY) {
			if (game.getPiece(currentX, currentY) != null) {
				countBetween++;
			}
			currentX += stepX;
			currentY += stepY;
		}
		Piece destPiece = game.getPiece(move.getDestination());
		if (destPiece == null) {
			return countBetween == 0;
		} else {
			if (countBetween == 1 && !destPiece.getPlayer().equals(game.getCurrentPlayer())) {
				return true;
			} else {
				return false;
			}
		}
	}
}
