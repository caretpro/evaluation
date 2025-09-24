package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;

/**
 * Knight piece that moves similar to knight in chess.
 * Rules of move of Knight can be found in wikipedia (https://en.wikipedia.org/wiki/Knight_(chess)).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Knight_(chess)'>Wikipedia</a>
 */
public class Knight extends Piece {
	public Knight(Player player) {
		super(player);
	}

	@Override
	public char getLabel() {
		return 'K';
	}

	private boolean validateMove(Game game, Move move) {
		var rules = new Rule[] { new OutOfBoundaryRule(), new OccupiedRule(), new VacantRule(), new NilMoveRule(),
				new FirstNMovesProtectionRule(game.getConfiguration().getNumMovesProtection()), new KnightMoveRule(),
				new KnightBlockRule(), };
		for (var rule : rules) {
			if (!rule.validate(game, move)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Move[] getAvailableMoves(Game game, Place source) {
		int[][] deltas = { { +2, +1 }, { +2, -1 }, { -2, +1 }, { -2, -1 }, { +1, +2 }, { +1, -2 }, { -1, +2 },
				{ -1, -2 } };
		var moves = new ArrayList<Move>();
		int x0 = source.getX();
		int y0 = source.getY();
		for (int[] d : deltas) {
			int x = x0 + d[0];
			int y = y0 + d[1];
			Place dest = new Place(x, y);
			Move mv = new Move(source, dest);
			if (validateMove(game, mv)) {
				moves.add(mv);
			}
		}
		return moves.toArray(Move[]::new);
	}
}
