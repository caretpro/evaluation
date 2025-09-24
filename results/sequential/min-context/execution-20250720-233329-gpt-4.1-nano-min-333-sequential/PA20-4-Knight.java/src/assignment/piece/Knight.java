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
		ArrayList<Move> validMoves = new ArrayList<>();
		int[][] moveOffsets = { { 2, 1 }, { 1, 2 }, { -1, 2 }, { -2, 1 }, { -2, -1 }, { -1, -2 }, { 1, -2 },
				{ 2, -1 } };
		int currentRow = source.row;
		int currentCol = source.col;
		for (int[] offset : moveOffsets) {
			int newRow = currentRow + offset[0];
			int newCol = currentCol + offset[1];
			if (game.getBoard().isWithinBounds(newRow, newCol)) {
				Place targetPlace = new Place(newRow, newCol);
				Move move = new Move(source, targetPlace);
				if (validateMove(game, move)) {
					validMoves.add(move);
				}
			}
		}
		return validMoves.toArray(new Move[0]);
	}
}
