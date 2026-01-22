package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;

/**
 * Archer piece that moves similar to cannon in chinese chess.
 * Rules of move of Archer can be found in wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class Archer extends Piece {
	public Archer(Player player) {
		super(player);
		//        throw new UnsupportedOperationException();
	}

	@Override
	public char getLabel() {
		return 'A';
	}

	private boolean validateMove(Game game, Move move) {
		var rules = new Rule[] { new OutOfBoundaryRule(), new OccupiedRule(), new VacantRule(), new NilMoveRule(),
				new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), new ArcherMoveRule(), };
		for (var rule : rules) {
			if (!rule.validate(game, move)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Move[] getAvailableMoves(Game game, Place source) {
		ArrayList<Move> validMoves = new ArrayList<>();
		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			int x = source.x() + dx;
			int y = source.y() + dy;
			boolean foundPiece = false;
			while (x >= 0 && x < game.getBoardWidth() && y >= 0 && y < game.getBoardHeight()) {
				Place dest = new Place(x, y);
				Move move = new Move(source, dest);
				if (!foundPiece) {
					if (game.getPiece(dest) == null) {
						if (validateMove(game, move)) {
							validMoves.add(move);
						}
					} else {
						foundPiece = true;
					}
				} else {
					if (game.getPiece(dest) != null) {
						if (game.getPiece(dest).getPlayer() != this.getPlayer()) {
							if (validateMove(game, move)) {
								validMoves.add(move);
							}
						}
						break;
					}
				}
				x += dx;
				y += dy;
			}
		}
		return validMoves.toArray(new Move[0]);
	}
}
