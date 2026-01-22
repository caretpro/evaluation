
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

		Position currentPosition = gameBoard.getPlayer().getOwner().getPosition();
		MoveResult moveResult = tryMove(currentPosition, direction);

		if (moveResult instanceof MoveResult.Invalid) {
			// Invalid move, do not mutate game board
			return moveResult;
		}

		if (moveResult instanceof MoveResult.Valid.Dead deadMove) {
			// Player dies, do not mutate game board
			return deadMove;
		}

		if (moveResult instanceof MoveResult.Valid.Alive aliveMove) {
			// Valid move and player alive, commit changes to game board

			Position origPos = aliveMove.origPosition;
			Position newPos = aliveMove.newPosition;

			// Get player entity from original position
			Cell origCell = gameBoard.getCell(origPos);
			if (!(origCell instanceof EntityCell origEntityCell)) {
				throw new IllegalStateException("Original cell does not contain an entity.");
			}
			Entity playerEntity = origEntityCell.getEntity();
			if (!(playerEntity instanceof Player player)) {
				throw new IllegalStateException("Original entity is not a player.");
			}

			// Replace original cell with StopCell at origPos
			gameBoard.setCell(origPos, new StopCell(origPos));

			// Remove collected gems from board by replacing with StopCell at their positions
			for (Position gemPos : aliveMove.collectedGems) {
				gameBoard.setCell(gemPos, new StopCell(gemPos));
			}

			// Remove collected extra lives from board by replacing with StopCell at their positions
			for (Position extraLifePos : aliveMove.collectedExtraLives) {
				gameBoard.setCell(extraLifePos, new StopCell(extraLifePos));
			}

			// Place player entity in new position
			gameBoard.setCell(newPos, new EntityCell(playerEntity));
			player.setPosition(newPos);

			// Increase player's lives by number of extra lives collected
			int extraLivesCollected = aliveMove.collectedExtraLives.size();
			if (extraLivesCollected > 0) {
				player.setLives(player.getLives() + extraLivesCollected);
			}

			return aliveMove;
		}

		// Should never reach here
		throw new IllegalStateException("Unknown MoveResult type.");
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

		if (prevMove instanceof MoveResult.Invalid) {
			// Invalid move did not change anything, nothing to undo
			return;
		}

		if (prevMove instanceof MoveResult.Valid.Dead) {
			// Dead move did not mutate board, nothing to undo
			return;
		}

		if (prevMove instanceof MoveResult.Valid.Alive aliveMove) {
			Position origPos = aliveMove.origPosition;
			Position newPos = aliveMove.newPosition;

			// Get player entity at new position
			Cell newCell = gameBoard.getCell(newPos);
			if (!(newCell instanceof EntityCell newEntityCell)) {
				throw new IllegalStateException("New cell does not contain an entity.");
			}
			Entity playerEntity = newEntityCell.getEntity();
			if (!(playerEntity instanceof Player player)) {
				throw new IllegalStateException("Entity at new position is not a player.");
			}

			// Remove player from new position by replacing with StopCell
			gameBoard.setCell(newPos, new StopCell(newPos));

			// Restore collected gems by placing new Gem entities wrapped in EntityCell at their positions
			for (Position gemPos : aliveMove.collectedGems) {
				gameBoard.setCell(gemPos, new EntityCell(new Gem()));
			}

			// Restore collected extra lives by placing new ExtraLife entities wrapped in EntityCell at their positions
			for (Position extraLifePos : aliveMove.collectedExtraLives) {
				gameBoard.setCell(extraLifePos, new EntityCell(new ExtraLife()));
			}

			// Place player back to original position
			gameBoard.setCell(origPos, new EntityCell(playerEntity));
			player.setPosition(origPos);

			// Decrease player's lives by number of extra lives collected
			int extraLivesCollected = aliveMove.collectedExtraLives.size();
			if (extraLivesCollected > 0) {
				player.setLives(player.getLives() - extraLivesCollected);
			}

			return;
		}

		throw new IllegalStateException("Unknown MoveResult type.");
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