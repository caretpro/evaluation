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
		int size = game.getConfiguration().getSize();
		var moves = new ArrayList<Move>();
		int[][] deltas = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		for (var d : deltas) {
			int dx = d[0], dy = d[1];
			int x = source.x() + dx, y = source.y() + dy;
			while (x >= 0 && x < size && y >= 0 && y < size) {
				Place dest = new Place(x, y);
				Move m = new Move(source, dest);
				if (game.getPiece(dest) != null) {
					break;
				}
				if (validateMove(game, m)) {
					moves.add(m);
				}
				x += dx;
				y += dy;
			}
			while (x >= 0 && x < size && y >= 0 && y < size && game.getPiece(new Place(x, y)) == null) {
				x += dx;
				y += dy;
			}
			x += dx;
			y += dy;
			if (x >= 0 && x < size && y >= 0 && y < size) {
				Place dest = new Place(x, y);
				Move m = new Move(source, dest);
				if (validateMove(game, m)) {
					moves.add(m);
				}
			}
		}
		return moves.toArray(Move[]::new);
	}
}
