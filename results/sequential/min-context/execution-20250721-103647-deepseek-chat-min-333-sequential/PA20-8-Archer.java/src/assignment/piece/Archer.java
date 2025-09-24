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
			boolean hasJumped = false;
			for (int step = 1;; step++) {
				Place target = new Place(source.x() + step * dx, source.y() + step * dy);
				Move move = new Move(source, target);
				if (!target.isValid()) {
					break;
				}
				if (!hasJumped) {
					if (game.getPiece(target) == null) {
						if (validateMove(game, move)) {
							validMoves.add(move);
						}
					} else {
						hasJumped = true;
					}
				} else {
					if (game.getPiece(target) != null) {
						if (game.getPiece(target).getPlayer() != this.getPlayer() && validateMove(game, move)) {
							validMoves.add(move);
						}
						break;
					}
				}
			}
		}
		return validMoves.toArray(new Move[0]);
	}
}
