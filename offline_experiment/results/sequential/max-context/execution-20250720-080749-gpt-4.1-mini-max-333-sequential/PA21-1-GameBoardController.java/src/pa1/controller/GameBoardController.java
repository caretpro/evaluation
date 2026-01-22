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

	/**
	 * Creates an instance.
	 * @param gameBoard  The instance of  {@link GameBoard}  to control.
	 */
	public void GameBoardController(final GameBoard gameBoard) {
		this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard cannot be null");
	}

	public MoveResult makeMove(final Direction direction) {
		Objects.requireNonNull(direction);
		final Position currentPos = gameBoard.getPlayer().getOwner().getPosition();
		final MoveResult moveResult = tryMove(currentPos, direction);
		if (moveResult instanceof MoveResult.Invalid) {
			return moveResult;
		}
		if (moveResult instanceof MoveResult.Valid.Dead deadMove) {
			return deadMove;
		}
		if (moveResult instanceof MoveResult.Valid.Alive aliveMove) {
			final Position newPos = aliveMove.newPosition;
			final Position origPos = aliveMove.origPosition;
			var origCell = gameBoard.getEntityCell(origPos);
			var newCell = gameBoard.getCell(newPos);
			if (origCell.getEntity() instanceof Player player) {
				gameBoard.board[origPos.row()][origPos.col()] = new EmptyCell(origPos);
				gameBoard.board[newPos.row()][newPos.col()] = new EntityCell(newPos, player);
				player.setPosition(newPos);
			} else {
				return new MoveResult.Invalid(currentPos);
			}
			for (Position gemPos : aliveMove.collectedGems) {
				gameBoard.board[gemPos.row()][gemPos.col()] = new EmptyCell(gemPos);
			}
			for (Position extraLifePos : aliveMove.collectedExtraLives) {
				gameBoard.board[extraLifePos.row()][extraLifePos.col()] = new EmptyCell(extraLifePos);
				gameBoard.getPlayer().incrementLives(1);
			}
			return aliveMove;
		}
		return new MoveResult.Invalid(currentPos);
	}

	void setCell(Position pos, Cell cell) {
		board[pos.row()][pos.col()] = cell;
	}
}
