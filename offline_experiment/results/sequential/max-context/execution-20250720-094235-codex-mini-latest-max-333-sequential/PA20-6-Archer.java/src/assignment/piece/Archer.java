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
		ArrayList<Move> moves = new ArrayList<>();
		int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		int size = game.getConfiguration().getSize();
		for (int[] d : dirs) {
			int dx = d[0], dy = d[1];
			int x = source.x() + dx, y = source.y() + dy;
			while (x >= 0 && x < size && y >= 0 && y < size) {
				Place dest = new Place(x, y);
				if (game.getPiece(dest) == null) {
					moves.add(new Move(source, dest));
					x += dx;
					y += dy;
				} else {
					break;
				}
			}
			if (x >= 0 && x < size && y >= 0 && y < size) {
				x += dx;
				y += dy;
				while (x >= 0 && x < size && y >= 0 && y < size) {
					Place dest = new Place(x, y);
					if (game.getPiece(dest) != null) {
						moves.add(new Move(source, dest));
						break;
					}
					x += dx;
					y += dy;
				}
			}
		}
		return moves.stream().filter(mv -> validateMove(game, mv)).toArray(Move[]::new);
	}
}
