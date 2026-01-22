
package assignment.piece;

import assignment.protocol.Game;
import assignment.protocol.Move;
import assignment.protocol.Piece;
import assignment.protocol.Place;
import assignment.protocol.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Archer piece that moves similar to cannon in chinese chess.
 * Rules of move of Archer can be found in wikipedia (https://en.wikipedia.org/wiki/Xiangqi#Cannon).
 *
 * @see <a href='https://en.wikipedia.org/wiki/Xiangqi#Cannon'>Wikipedia</a>
 */
public class Archer extends Piece {
	public Archer(Player player) {
		super(player);
	}

	@Override
	public char getLabel() {
		return 'A';
	}

	/**
	 * Returns an array of moves that are valid given the current place of the piece.
	 * Given the {@link Game} object and the {@link Place} that current knight piece locates, this method should
	 * return ALL VALID {@link Move}s according to the current {@link Place} of this knight piece.
	 * All the returned {@link Move} should have source equal to the source parameter.
	 * <p>
	 * Hint: you should consider corner cases when the {@link Move} is not valid on the gameboard.
	 * Several tests are provided and your implementation should pass them.
	 * <p>
	 * <strong>Attention: Student should make sure all {@link Move}s returned are valid.</strong>
	 *
	 * @param game   the game object
	 * @param source the current place of the piece
	 * @return an array of available moves
	 */
	@Override
	public Move[] getAvailableMoves(Game game, Place source) {
		List<Move> moves = new ArrayList<>();
		int rows = game.getConfiguration().rows;
		int cols = game.getConfiguration().cols;

		int srcRow = source.row;
		int srcCol = source.col;

		// Directions: up, down, left, right
		int[][] directions = {
			{-1, 0}, // up
			{1, 0},  // down
			{0, -1}, // left
			{0, 1}   // right
		};

		for (int[] dir : directions) {
			int dRow = dir[0];
			int dCol = dir[1];

			// Step 1: move along empty squares before the screen
			int r = srcRow + dRow;
			int c = srcCol + dCol;

			// Move along empty squares, add moves to empty squares
			while (r >= 0 && r < rows && c >= 0 && c < cols) {
				Place dest = new Place(r, c);
				Piece p = game.getPiece(dest);
				if (p == null) {
					Move move = new Move(source, dest);
					if (validateMove(game, move)) {
						moves.add(move);
					}
					r += dRow;
					c += dCol;
				} else {
					// Found the screen piece, break to next step
					break;
				}
			}

			// Step 2: after the screen, find the first piece that can be captured
			r += dRow;
			c += dCol;
			while (r >= 0 && r < rows && c >= 0 && c < cols) {
				Place dest = new Place(r, c);
				Piece p = game.getPiece(dest);
				if (p == null) {
					// empty square after screen, keep looking
					r += dRow;
					c += dCol;
				} else {
					// Found a piece after screen, can capture if opponent
					if (!p.getPlayer().equals(this.getPlayer())) {
						Move move = new Move(source, dest);
						if (validateMove(game, move)) {
							moves.add(move);
						}
					}
					// Stop after first piece after screen
					break;
				}
			}
		}

		return moves.toArray(new Move[0]);
	}

	private boolean validateMove(Game game, Move move) {
		var rules = new Rule[] { new OutOfBoundaryRule(), new OccupiedRule(), new VacantRule(), new NilMoveRule(),
				new FirstNMovesProtectionRule(game.getConfiguration().numMovesProtection), new ArcherMoveRule(), };
		for (var rule : rules) {
			if (!rule.validate(game, move)) {
				return false;
			}
		}
		return true;
	}
}