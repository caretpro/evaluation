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
		int[] dx = { 1, 2, 2, 1, -1, -2, -2, -1 };
		int[] dy = { 2, 1, -1, -2, -2, -1, 1, 2 };
		ArrayList<Move> validMoves = new ArrayList<>();
		int row = source.row;
		int col = source.column;
		for (int i = 0; i < dx.length; i++) {
			int newRow = row + dy[i];
			int newCol = col + dx[i];
			Place destination = new Place(newRow, newCol);
			Move move = new Move(source, destination);
			if (validateMove(game, move)) {
				validMoves.add(move);
			}
		}
		return validMoves.toArray(new Move[0]);
	}
}
