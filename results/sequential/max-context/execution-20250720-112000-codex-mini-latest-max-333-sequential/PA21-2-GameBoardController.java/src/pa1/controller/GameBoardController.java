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
		this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
	}

	@Override
	public MoveResult makeMove(final Direction direction) {
		final Position origPos = gameBoard.getPlayer().getOwner().getPosition();
		MoveResult result = tryMove(origPos, direction);
		if (result instanceof MoveResult.Valid.Alive alive) {
			Position newPos = alive.newPosition;
			gameBoard.board[origPos.row()][origPos.col()] = new EmptyCell();
			for (Position gemPos : alive.collectedGems) {
				gameBoard.board[gemPos.row()][gemPos.col()] = new EmptyCell();
			}
			for (Position lifePos : alive.collectedExtraLives) {
				gameBoard.board[lifePos.row()][lifePos.col()] = new EmptyCell();
				gameBoard.getPlayer().gainExtraLife();
			}
			gameBoard.board[newPos.row()][newPos.col()] = new EntityCell<>(gameBoard.getPlayer());
			gameBoard.getPlayer().getOwner().setPosition(newPos);
		}
		return result;
	}

	@Override
	public MoveResult makeMove(final Direction direction) {
		final Position origPos = gameBoard.getPlayer().getOwner().getPosition();
		MoveResult result = tryMove(origPos, direction);
		if (result instanceof MoveResult.Valid.Alive alive) {
			Position newPos = alive.newPosition;
			gameBoard.board[origPos.row()][origPos.col()] = new EmptyCell();
			for (Position gemPos : alive.collectedGems) {
				gameBoard.board[gemPos.row()][gemPos.col()] = new EmptyCell();
			}
			for (Position lifePos : alive.collectedExtraLives) {
				gameBoard.board[lifePos.row()][lifePos.col()] = new EmptyCell();
				gameBoard.getPlayer().gainExtraLife();
			}
			gameBoard.board[newPos.row()][newPos.col()] = new EntityCell<>(gameBoard.getPlayer());
			gameBoard.getPlayer().getOwner().setPosition(newPos);
		}
		return result;
	}
}
