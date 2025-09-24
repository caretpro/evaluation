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

	public void GameBoardController(final GameBoard gameBoard) {
		this.gameBoard = Objects.requireNonNull(gameBoard);
	}

	public MoveResult makeMove(final Direction direction) {
		Objects.requireNonNull(direction);
		final Position originalPosition = gameBoard.getPlayer().getOwner().getPosition();
		final MoveResult moveResult = tryMove(originalPosition, direction);
		if (moveResult instanceof MoveResult.Valid validResult) {
			if (validResult instanceof MoveResult.Valid.Alive aliveResult) {
				final Position prevPosition = originalPosition;
				final Position newPosition = aliveResult.newPosition;
				final var collectedGems = aliveResult.collectedGems;
				final var collectedExtraLives = aliveResult.collectedExtraLives;
				final Cell[][] previousCells = new Cell[gameBoard.getNumRows()][gameBoard.getNumCols()];
				for (int r = 0; r < gameBoard.getNumRows(); r++) {
					System.arraycopy(gameBoard.getRow(r), 0, previousCells[r], 0, gameBoard.getNumCols());
				}
				gameBoard.getCell(prevPosition).removeEntity();
				gameBoard.getEntityCell(newPosition).setEntity(gameBoard.getPlayer());
				for (final var gemPos : collectedGems) {
					gameBoard.getCell(gemPos).removeEntity();
				}
				for (final var lifePos : collectedExtraLives) {
					gameBoard.getCell(lifePos).removeEntity();
				}
				return new MoveResult.Valid.Alive(newPosition, prevPosition, collectedGems, collectedExtraLives);
			} else {
				return moveResult;
			}
		} else {
			return moveResult;
		}
	}

	public void undoMove(final MoveResult prevMove) {
		if (prevMove == null) {
			return;
		}
		if (prevMove instanceof MoveResult.Valid validResult) {
			Position prevPosition = validResult.origPosition;
			Position currentPosition = gameBoard.getPlayer().getOwner().getPosition();
			gameBoard.getCell(currentPosition).setEntity(null);
			gameBoard.getEntityCell(prevPosition).setEntity(gameBoard.getPlayer());
			if (validResult instanceof MoveResult.Valid.Alive aliveResult) {
				for (Position gemPos : aliveResult.collectedGems) {
					gameBoard.getEntityCell(gemPos).setEntity(new Gem());
				}
				for (Position lifePos : aliveResult.collectedExtraLives) {
					gameBoard.getEntityCell(lifePos).setEntity(new ExtraLife());
				}
			} else if (validResult instanceof MoveResult.Valid.Dead deadResult) {
				gameBoard.getEntityCell(deadResult.minePosition).setEntity(new Mine());
			}
		}
	}
}
