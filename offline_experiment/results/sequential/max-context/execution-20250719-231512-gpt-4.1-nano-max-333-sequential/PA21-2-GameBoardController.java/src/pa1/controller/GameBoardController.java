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
		Position currentPosition = gameBoard.getPlayer().getOwner().getPosition();
		MoveResult moveResult = tryMove(currentPosition, direction);
		if (moveResult instanceof MoveResult.Invalid) {
			return moveResult;
		}
		if (moveResult instanceof MoveResult.Valid.Dead deadResult) {
			return deadResult;
		}
		if (moveResult instanceof MoveResult.Valid.Alive aliveResult) {
			Position oldPos = aliveResult.origPosition;
			Position newPos = aliveResult.newPosition;
			Cell oldCell = gameBoard.getCell(oldPos);
			if (oldCell instanceof EntityCell) {
			}
			for (Position gemPos : aliveResult.collectedGems) {
			}
			for (Position lifePos : aliveResult.collectedExtraLives) {
			}
			gameBoard.getPlayer().getOwner().setPosition(newPos);
			return new MoveResult.Valid.Alive(newPos, oldPos, aliveResult.collectedGems,
					aliveResult.collectedExtraLives);
		}
		return new MoveResult.Invalid(currentPosition);
	}

	public void undoMove(final MoveResult prevMove) {
		if (prevMove == null) {
			return;
		}
		Position restorePosition;
		if (prevMove instanceof MoveResult.Valid validResult) {
			restorePosition = validResult.origPosition;
		} else {
			return;
		}
		gameBoard.getPlayer().getOwner().setPosition(restorePosition);
		if (prevMove instanceof MoveResult.Valid.Dead deadResult) {
			gameBoard.getPlayer().getOwner().setPosition(deadResult.newPosition);
		} else if (prevMove instanceof MoveResult.Valid.Alive aliveResult) {
			Cell[][] currentCells = new Cell[gameBoard.getNumRows()][gameBoard.getNumCols()];
			for (int r = 0; r < gameBoard.getNumRows(); r++) {
				for (int c = 0; c < gameBoard.getNumCols(); c++) {
					currentCells[r][c] = gameBoard.getCell(r, c);
				}
			}
			for (Position gemPos : aliveResult.collectedGems) {
				currentCells[gemPos.row()][gemPos.col()] = new Gem(gemPos);
			}
			for (Position lifePos : aliveResult.collectedExtraLives) {
				currentCells[lifePos.row()][lifePos.col()] = new ExtraLife(lifePos);
			}
			gameBoard = new GameBoard(gameBoard.getNumRows(), gameBoard.getNumCols(), currentCells);
		}
	}
}
