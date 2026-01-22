
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
		ArrayList<Move> moves = new ArrayList<>();
		int boardWidth = game.getConfiguration().cols();
		int boardHeight = game.getConfiguration().rows();

		int srcX = source.x();
		int srcY = source.y();

		// Directions: up, down, left, right
		int[][] directions = {
			{0, -1}, // up
			{0, 1},  // down
			{-1, 0}, // left
			{1, 0}   // right
		};

		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];

			// Step 1: Move along direction until hitting a piece or boundary
			int x = srcX + dx;
			int y = srcY + dy;

			// Move through empty squares - these are valid non-capturing moves
			while (x >= 0 && x < boardWidth && y >= 0 && y < boardHeight) {
				Place dest = new Place(x, y);
				Piece p = game.getPiece(dest);
				if (p != null) {
					// Found the screen piece, stop scanning empty squares
					break;
				}
				// Empty square, create move and validate
				Move move = new Move(source, dest);
				if (validateMove(game, move)) {
					moves.add(move);
				}
				x += dx;
				y += dy;
			}

			// Step 2: Find the screen piece (the first piece in this direction)
			if (x < 0 || x >= boardWidth || y < 0 || y >= boardHeight) {
				// No screen piece found in this direction
				continue;
			}
			Place screenPlace = new Place(x, y);
			Piece screenPiece = game.getPiece(screenPlace);
			if (screenPiece == null) {
				// No screen piece found
				continue;
			}

			// Step 3: After the screen, look for a target piece to capture
			x += dx;
			y += dy;
			while (x >= 0 && x < boardWidth && y >= 0 && y < boardHeight) {
				Place dest = new Place(x, y);
				Piece p = game.getPiece(dest);
				if (p != null) {
					// Found a piece beyond the screen, can capture if opponent's piece
					if (!p.getPlayer().equals(this.getPlayer())) {
						Move move = new Move(source, dest);
						if (validateMove(game, move)) {
							moves.add(move);
						}
					}
					// Whether capture or not, stop scanning further in this direction
					break;
				}
				// Empty square beyond screen cannot be moved to
				x += dx;
				y += dy;
			}
		}

		return moves.toArray(new Move[0]);
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