
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
		int boardSize = game.getConfiguration().getBoardSize();

		// Check horizontal moves
		addHorizontalMoves(game, source, moves, boardSize);

		// Check vertical moves
		addVerticalMoves(game, source, moves, boardSize);

		return moves.toArray(new Move[0]);
	}

	private void addHorizontalMoves(Game game, Place source, List<Move> moves, int boardSize) {
		// Check moves to the right
		addMovesInDirection(game, source, moves, 1, 0, boardSize);

		// Check moves to the left
		addMovesInDirection(game, source, moves, -1, 0, boardSize);
	}

	private void addVerticalMoves(Game game, Place source, List<Move> moves, int boardSize) {
		// Check moves upwards
		addMovesInDirection(game, source, moves, 0, 1, boardSize);

		// Check moves downwards
		addMovesInDirection(game, source, moves, 0, -1, boardSize);
	}

	private void addMovesInDirection(Game game, Place source, List<Move> moves, int dx, int dy, int boardSize) {
		int x = source.getX();
		int y = source.getY();
		int obstacleCount = 0;

		for (int i = 1; i < boardSize; i++) {
			int newX = x + i * dx;
			int newY = y + i * dy;

			if (newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize) {
				Place destination = new Place(newX, newY);
				Move move = new Move(source, destination);

				Piece pieceAtDestination = game.getPiece(destination);

				if (pieceAtDestination == null) {
					if (obstacleCount == 0) {
						if (validateMove(game, move)) {
							moves.add(move);
						}
					}
				} else {
					obstacleCount++;
					if (obstacleCount == 1) {
						// First obstacle encountered
						continue;
					} else if (obstacleCount == 2) {
						// Second obstacle encountered, try to capture
						if (pieceAtDestination.getPlayer() != this.getPlayer()) {
							if (validateMove(game, move)) {
								moves.add(move);
							}
						}
						break; // Stop after the second obstacle
					} else {
						break; // More than two obstacles, stop
					}
				}
			} else {
				break; // Out of bounds
			}
		}
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