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
		Position playerPosition = null;
		for (int row = 0; row < gameBoard.getNumRows(); row++) {
			for (int col = 0; col < gameBoard.getNumCols(); col++) {
				Position pos = new Position(row, col);
				Cell cell = gameBoard.getCell(pos);
				if (cell instanceof EntityCell entityCell) {
					if (entityCell.getEntity() instanceof Player) {
						playerPosition = pos;
						break;
					}
				}
			}
			if (playerPosition != null) {
				break;
			}
		}
		if (playerPosition == null) {
			return new MoveResult.Invalid(null);
		}
		MoveResult moveResult = tryMove(playerPosition, direction);
		if (moveResult instanceof MoveResult.Valid validResult) {
			if (validResult instanceof MoveResult.Valid.Alive aliveResult) {
				gameBoard.setCell(playerPosition, new EmptyCell());
				gameBoard.setCell(aliveResult.getNewPosition(), new EntityCell(new Player()));
				return validResult;
			} else {
				return validResult;
			}
		} else {
			return moveResult;
		}
	}

	public void undoMove(final MoveResult prevMove) {
		if (prevMove == null) {
			return;
		}
		Position currentPlayerPos = null;
		for (int row = 0; row < gameBoard.getNumRows(); row++) {
			for (int col = 0; col < gameBoard.getNumCols(); col++) {
				Position pos = new Position(row, col);
				Cell cell = gameBoard.getCell(pos);
				if (cell instanceof EntityCell entityCell && entityCell.getEntity() instanceof Player) {
					currentPlayerPos = pos;
					break;
				}
			}
			if (currentPlayerPos != null) {
				break;
			}
		}
		Position prevPosition = null;
		ArrayList<Position> prevGems = new ArrayList<>();
		ArrayList<Position> prevExtraLives = new ArrayList<>();
		if (prevMove instanceof MoveResult.Valid validMove) {
			prevPosition = validMove.previous();
			prevGems.addAll(validMove.collectedGems());
			prevExtraLives.addAll(validMove.collectedExtraLives());
		} else if (prevMove instanceof MoveResult.Valid.Dead deadMove) {
			prevPosition = deadMove.previous();
		} else if (prevMove instanceof MoveResult.Invalid) {
			return;
		} else {
			return;
		}
		if (currentPlayerPos != null) {
			gameBoard.setCell(currentPlayerPos, new EmptyCell());
		}
		gameBoard.setCell(prevPosition, new EntityCell(new Player()));
		for (Position gemPos : prevGems) {
			gameBoard.setCell(gemPos, new EntityCell(new Gem()));
		}
		for (Position lifePos : prevExtraLives) {
			gameBoard.setCell(lifePos, new EntityCell(new ExtraLife()));
		}
	}
}
