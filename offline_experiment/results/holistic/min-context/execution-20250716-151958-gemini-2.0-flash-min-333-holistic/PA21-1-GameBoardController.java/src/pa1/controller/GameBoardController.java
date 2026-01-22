
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
		Position currentPosition = gameBoard.getPlayerPosition();
		MoveResult moveResult = tryMove(currentPosition, direction);

		if (moveResult instanceof MoveResult.Valid validMove) {
			if (validMove instanceof MoveResult.Valid.Alive aliveMove) {
				// Update player position
				gameBoard.setPlayerPosition(aliveMove.getNewPosition());

				// Collect gems
				for (Position gemPosition : aliveMove.getCollectedGems()) {
					EntityCell cell = (EntityCell) gameBoard.getCell(gemPosition);
					gameBoard.removeEntity(cell.getEntity());
				}

				// Collect extra lives
				for (Position extraLifePosition : aliveMove.getCollectedExtraLives()) {
					EntityCell cell = (EntityCell) gameBoard.getCell(extraLifePosition);
					gameBoard.removeEntity(cell.getEntity());
					gameBoard.incrementLives();
				}

				return aliveMove;
			} else if (validMove instanceof MoveResult.Valid.Dead deadMove) {
				// Player dies
				gameBoard.decrementLives();
				if (gameBoard.getLives() <= 0) {
					return deadMove;
				} else {
					gameBoard.setPlayerPosition(deadMove.getOriginalPosition());
					return deadMove;
				}
			}
		}

		return moveResult;
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
		if (prevMove instanceof MoveResult.Valid validMove) {
			if (validMove instanceof MoveResult.Valid.Alive aliveMove) {
				// Revert player position
				gameBoard.setPlayerPosition(aliveMove.getOriginalPosition());

				// Restore gems
				for (Position gemPosition : aliveMove.getCollectedGems()) {
					gameBoard.addEntity(gemPosition, new Gem());
				}

				// Restore extra lives
				for (Position extraLifePosition : aliveMove.getCollectedExtraLives()) {
					gameBoard.addEntity(extraLifePosition, new ExtraLife());
				}
			} else if (validMove instanceof MoveResult.Valid.Dead deadMove) {
				// Revert player position
				gameBoard.setPlayerPosition(deadMove.getOriginalPosition());
				gameBoard.incrementLives();
			}
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