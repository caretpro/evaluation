
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
		int rows = game.getConfiguration().rows();
		int cols = game.getConfiguration().cols();

		int srcRow = source.row();
		int srcCol = source.col();

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

			// Step 1: Move along direction through empty squares (vacant moves)
			int r = srcRow + dRow;
			int c = srcCol + dCol;
			while (r >= 0 && r < rows && c >= 0 && c < cols) {
				Place dest = new Place(r, c);
				Piece p = game.getPiece(dest);
				if (p == null) {
					// Empty square, can move here
					Move move = new Move(source, dest);
					if (validateMove(game, move)) {
						moves.add(move);
					}
					r += dRow;
					c += dCol;
				} else {
					// Found the screen piece, break to step 2
					break;
				}
			}

			// Step 2: Find the screen piece (must be exactly one piece between source and target)
			// If no screen piece found, no capture moves in this direction
			if (r < 0 || r >= rows || c < 0 || c >= cols) {
				// Out of boundary, no screen piece
				continue;
			}
			Place screenPlace = new Place(r, c);
			Piece screenPiece = game.getPiece(screenPlace);
			if (screenPiece == null) {
				// No screen piece, no capture moves
				continue;
			}

			// Step 3: After screen piece, move forward to find capture target (must be opponent piece)
			r += dRow;
			c += dCol;
			while (r >= 0 && r < rows && c >= 0 && c < cols) {
				Place dest = new Place(r, c);
				Piece p = game.getPiece(dest);
				if (p == null) {
					// Empty square after screen piece, cannon cannot move here
					r += dRow;
					c += dCol;
					continue;
				} else {
					// Found a piece after screen piece
					if (!p.getPlayer().equals(this.getPlayer())) {
						// Opponent piece can be captured
						Move move = new Move(source, dest);
						if (validateMove(game, move)) {
							moves.add(move);
						}
					}
					// Whether capture or not, stop searching further in this direction
					break;
				}
			}
		}

		return moves.toArray(Move[]::new);
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
}