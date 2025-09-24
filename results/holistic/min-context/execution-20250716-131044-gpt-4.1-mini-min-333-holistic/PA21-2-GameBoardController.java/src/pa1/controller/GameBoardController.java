
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
		if (player == null) {
			// No player on the board, invalid move
			return new MoveResult.Invalid(null);
		}
		Position playerPos = player.getPosition();
		if (playerPos == null) {
			// Player has no position, invalid move
			return new MoveResult.Invalid(null);
		}

		final MoveResult moveResult = tryMove(playerPos, direction);

		if (moveResult instanceof MoveResult.Valid.Alive aliveMove) {
			// Remove player from old position by setting an empty cell
			gameBoard.setCell(aliveMove.oldPosition, new EmptyCell(aliveMove.oldPosition));

			// Remove collected gems
			for (Position gemPos : aliveMove.collectedGems) {
				gameBoard.setCell(gemPos, new EmptyCell(gemPos));
			}

			// Remove collected extra lives
			for (Position lifePos : aliveMove.collectedExtraLives) {
				gameBoard.setCell(lifePos, new EmptyCell(lifePos));
			}

			// Place player in new position
			gameBoard.setCell(aliveMove.newPosition, new EntityCell(aliveMove.newPosition, new Player()));

			// Update player position
			player.setPosition(aliveMove.newPosition);

			// Increase player lives for extra lives collected
			int currentLives = player.getLives();
			int extraLivesCount = aliveMove.collectedExtraLives.size();
			player.setLives(currentLives + extraLivesCount);

			return aliveMove;
		} else if (moveResult instanceof MoveResult.Valid.Dead deadMove) {
			// Player dies, do not mutate the board, just return the dead move result
			return deadMove;
		} else {
			// Invalid move, no mutation
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
			// Remove player from new position
			gameBoard.setCell(aliveMove.newPosition, new EmptyCell(aliveMove.newPosition));

			// Restore collected gems
			for (Position gemPos : aliveMove.collectedGems) {
				gameBoard.setCell(gemPos, new EntityCell(gemPos, new Gem()));
			}

			// Restore collected extra lives
			for (Position lifePos : aliveMove.collectedExtraLives) {
				gameBoard.setCell(lifePos, new EntityCell(lifePos, new ExtraLife()));
			}

			// Place player back to old position
			gameBoard.setCell(aliveMove.oldPosition, new EntityCell(aliveMove.oldPosition, new Player()));

			// Update player position
			Player player = gameBoard.getPlayer();
			if (player != null) {
				player.setPosition(aliveMove.oldPosition);

				// Decrease player lives for extra lives collected
				int currentLives = player.getLives();
				int extraLivesCount = aliveMove.collectedExtraLives.size();
				player.setLives(currentLives - extraLivesCount);
			}
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