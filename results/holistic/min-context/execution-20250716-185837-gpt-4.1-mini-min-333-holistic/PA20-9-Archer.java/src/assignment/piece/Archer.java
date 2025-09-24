
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
		int boardWidth = game.getConfiguration().getWidth();
		int boardHeight = game.getConfiguration().getHeight();

		int srcX = source.getX();
		int srcY = source.getY();

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

			// Phase 1: move along direction until first piece (screen) or boundary
			int x = srcX + dx;
			int y = srcY + dy;
			while (x >= 0 && x < boardWidth && y >= 0 && y < boardHeight) {
				Place dest = Place.at(x, y);
				Piece p = game.getPieceAt(dest);
				if (p == null) {
					// vacant square - normal move
					Move move = Move.move(source, dest);
					if (validateMove(game, move)) {
						moves.add(move);
					}
					x += dx;
					y += dy;
				} else {
					// found screen piece, stop phase 1
					break;
				}
			}

			// Phase 2: after screen, look for capture move - must jump exactly one piece (the screen)
			if (x >= 0 && x < boardWidth && y >= 0 && y < boardHeight) {
				// skip the screen piece
				x += dx;
				y += dy;
				while (x >= 0 && x < boardWidth && y >= 0 && y < boardHeight) {
					Place dest = Place.at(x, y);
					Piece p = game.getPieceAt(dest);
					if (p == null) {
						// empty square beyond screen, keep looking
						x += dx;
						y += dy;
					} else {
						// found a piece beyond screen, can capture if opponent piece
						Move move = Move.move(source, dest);
						if (validateMove(game, move)) {
							moves.add(move);
						}
						break; // only one capture possible beyond screen
					}
				}
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