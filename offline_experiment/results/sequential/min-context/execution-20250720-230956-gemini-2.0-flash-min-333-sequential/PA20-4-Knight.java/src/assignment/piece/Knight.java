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

	/**
	 * Returns an array of moves that are valid given the current place of the piece. Given the  {@link Game}  object and the  {@link Place}  that current knight piece locates, this method should return ALL VALID  {@link Move} s according to the current  {@link Place}  of this knight piece. All the returned  {@link Move}  should have source equal to the source parameter. <p> Hint: you should consider corner cases when the  {@link Move}  is not valid on the gameboard. Several tests are provided and your implementation should pass them. <p> <strong>Attention: Student should make sure all  {@link Move} s returned are valid.</strong>
	 * @param game    the game object
	 * @param source  the current place of the piece
	 * @return  an array of available moves
	 */
	@Override
	public Move[] getAvailableMoves(Game game, Place source) {
		ArrayList<Move> moves = new ArrayList<>();
		int[] rowMoves = { -2, -2, -1, -1, 1, 1, 2, 2 };
		int[] colMoves = { -1, 1, -2, 2, -2, 2, -1, 1 };
		for (int i = 0; i < 8; i++) {
			int newRow = source.getRow() + rowMoves[i];
			int newCol = source.getCol() + colMoves[i];
			Place destination = new Place(newRow, newCol);
			Move move = new Move(source, destination);
			if (validateMove(game, move)) {
				moves.add(move);
			}
		}
		return moves.toArray(new Move[0]);
	}
}
