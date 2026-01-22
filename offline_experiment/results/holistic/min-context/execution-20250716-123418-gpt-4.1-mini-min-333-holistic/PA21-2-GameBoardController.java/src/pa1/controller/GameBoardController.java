
package pa1.controller;

import pa1.model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller for {@link GameBoard}.
 *
 * <p>
 * This class is responsible for providing high-level operations to mutate a {@link GameBoard}. This should be the only
 * class which mutates the game board; Other classes should use this class to mutate the game board.
 * </p>
 */
public class GameBoardController {

	@NotNull
	private final GameBoard gameBoard;

	/**
	 * Creates an instance.
	 *
	 * @param gameBoard The instance of {@link GameBoard} to control.
	 */
	public GameBoardController(final GameBoard gameBoard) {
		Objects.requireNonNull(gameBoard);
		this.gameBoard = gameBoard;
	}

	/**
	 * Moves the player in the given direction.
	 *
	 * <p>
	 * You should ensure that the game board is only mutated if the move is valid and results in the player still being
	 * alive. If the player dies after moving or the move is invalid, the game board should remain in the same state as
	 * before this method was called.
	 * </p>
	 *
	 * @param direction Direction to move the player in.
	 * @return An instance of {@link MoveResult} representing the result of this action.
	 */
	public MoveResult makeMove(final Direction direction) {
		Objects.requireNonNull(direction);

		Player player = gameBoard.getPlayer();
		Position currentPosition = player.getPosition();
		MoveResult moveResult = tryMove(currentPosition, direction);

		if (moveResult instanceof MoveResult.Valid.Alive aliveMove) {
			// Move is valid and player survives, so mutate the game board accordingly

			// Remove player from old position
			gameBoard.setCell(currentPosition, new Empty());

			// Collect gems
			for (Position gemPos : aliveMove.collectedGems) {
				gameBoard.setCell(gemPos, new Empty());
			}

			// Collect extra lives
			for (Position lifePos : aliveMove.collectedExtraLives) {
				gameBoard.setCell(lifePos, new Empty());
			}

			// Place player at new position
			gameBoard.setCell(aliveMove.newPosition, new EntityCell(player));

			// Update player position
			player.setPosition(aliveMove.newPosition);

			// If Player class has methods to update lives and gems, use them here.
			// Otherwise, omit these lines or implement accordingly.
			// Example:
			// player.addExtraLives(aliveMove.collectedExtraLives.size());
			// player.addCollectedGems(aliveMove.collectedGems.size());

			return aliveMove;
		} else if (moveResult instanceof MoveResult.Valid.Dead deadMove) {
			// Player dies, do not mutate the board, just return the result
			return deadMove;
		} else {
			// Invalid move, do not mutate the board
			return moveResult;
		}
	}

	/**
	 * Undoes a move by reverting all changes performed by the specified move.
	 *
	 * <p>
	 * Hint: Undoing a move is effectively the same as reversing everything you have done to make a move.
	 * </p>
	 *
	 * @param prevMove The {@link MoveResult} object to revert.
	 */
	public void undoMove(final MoveResult prevMove) {
		Objects.requireNonNull(prevMove);

		if (prevMove instanceof MoveResult.Valid.Alive aliveMove) {
			Player player = gameBoard.getPlayer();

			// Remove player from current position
			gameBoard.setCell(aliveMove.newPosition, new Empty());

			// Restore collected gems
			for (Position gemPos : aliveMove.collectedGems) {
				gameBoard.setCell(gemPos, new EntityCell(new Gem()));
			}

			// Restore collected extra lives
			for (Position lifePos : aliveMove.collectedExtraLives) {
				gameBoard.setCell(lifePos, new EntityCell(new ExtraLife()));
			}

			// Place player back to old position
			gameBoard.setCell(aliveMove.oldPosition, new EntityCell(player));

			// Update player position
			player.setPosition(aliveMove.oldPosition);

			// If Player class has methods to revert lives and gems, use them here.
			// Otherwise, omit these lines or implement accordingly.
			// Example:
			// player.removeExtraLives(aliveMove.collectedExtraLives.size());
			// player.removeCollectedGems(aliveMove.collectedGems.size());

		} else if (prevMove instanceof MoveResult.Valid.Dead) {
			// No mutation was done on death, so nothing to undo
		} else if (prevMove instanceof MoveResult.Invalid) {
			// No mutation was done on invalid move, so nothing to undo
		}
	}

	/**
	 * Tries to move the player from a position in the specified direction as far as possible.
	 *
	 * <p>
	 * Note that this method does <b>NOT</b> actually move the player. It just tries to move the player and return
	 * the state of the player as-if it has been moved.
	 * </p>
	 *
	 * @param position  The original position of the player.
	 * @param direction The direction to move the player in.
	 * @return An instance of {@link MoveResult} representing the type of the move and the position of the player after
	 * moving.
	 */
	@NotNull
	private MoveResult tryMove(@NotNull final Position position, @NotNull final Direction direction) {
		Objects.requireNonNull(position);
		Objects.requireNonNull(direction);

		final var collectedGems = new ArrayList<Position>();
		final var collectedExtraLives = new ArrayList<Position>();
		Position lastValidPosition = position;
		do {
			final Position newPosition = offsetPosition(lastValidPosition, direction);
			if (newPosition == null) {
				break;
			}

			lastValidPosition = newPosition;

			if (gameBoard.getCell(newPosition) instanceof StopCell) {
				break;
			}

			if (gameBoard.getCell(newPosition) instanceof EntityCell entityCell) {
				if (entityCell.getEntity() instanceof Mine) {
					return new MoveResult.Valid.Dead(position, newPosition);
				}

				if (entityCell.getEntity() instanceof Gem) {
					collectedGems.add(newPosition);
				} else if (entityCell.getEntity() instanceof ExtraLife) {
					collectedExtraLives.add(newPosition);
				}
			}
		} while (true);

		if (lastValidPosition.equals(position)) {
			return new MoveResult.Invalid(position);
		}

		return new MoveResult.Valid.Alive(lastValidPosition, position, collectedGems, collectedExtraLives);
	}

	/**
	 * Offsets the {@link Position} in the specified {@link Direction} by one step.
	 *
	 * @param position  The original position.
	 * @param direction The direction to offset.
	 * @return The given position offset by one in the specified direction. If the new position is outside of the game
	 * board, or contains a non-{@link EntityCell}, returns {@code null}.
	 */
	@Nullable
	private Position offsetPosition(@NotNull final Position position, @NotNull final Direction direction) {
		Objects.requireNonNull(position);
		Objects.requireNonNull(direction);

		final var newPos = position.offsetByOrNull(direction.getOffset(), gameBoard.getNumRows(),
				gameBoard.getNumCols());

		if (newPos == null) {
			return null;
		}
		if (!(gameBoard.getCell(newPos) instanceof EntityCell)) {
			return null;
		}

		return newPos;
	}
}