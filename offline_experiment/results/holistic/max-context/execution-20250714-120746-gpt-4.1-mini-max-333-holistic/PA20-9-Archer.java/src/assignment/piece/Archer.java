
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
		int size = game.getConfiguration().getSize();
		int x = source.x();
		int y = source.y();

		// Directions: up, down, left, right
		int[][] directions = {
			{0, 1},  // up
			{0, -1}, // down
			{-1, 0}, // left
			{1, 0}   // right
		};

		for (int[] dir : directions) {
			int dx = dir[0];
			int dy = dir[1];
			boolean screenFound = false;
			int cx = x + dx;
			int cy = y + dy;

			while (cx >= 0 && cx < size && cy >= 0 && cy < size) {
				Place currentPlace = new Place(cx, cy);
				Piece pieceAtCurrent = game.getPiece(currentPlace);

				if (!screenFound) {
					if (pieceAtCurrent == null) {
						// No screen found yet, empty square: valid move (non-capturing)
						Move move = new Move(source, currentPlace);
						if (validateMove(game, move)) {
							moves.add(move);
						}
					} else {
						// Found the screen piece
						screenFound = true;
					}
				} else {
					// Screen found, next piece must be captured if opponent's piece
					if (pieceAtCurrent != null) {
						// If opponent's piece, can capture
						if (!pieceAtCurrent.getPlayer().equals(this.getPlayer())) {
							Move move = new Move(source, currentPlace);
							if (validateMove(game, move)) {
								moves.add(move);
							}
						}
						// Whether captured or not, stop searching in this direction
						break;
					} else {
						// Empty square after screen is not valid for move
						// Continue searching for a piece to capture
						cx += dx;
						cy += dy;
						continue;
					}
				}
				cx += dx;
				cy += dy;
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