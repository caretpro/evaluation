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
		this.gameBoard = Objects.requireNonNull(gameBoard, "gameBoard must not be null");
	}

	public MoveResult makeMove(final Direction direction) {
		final Position startPos = gameBoard.getPlayerPosition();
		final MoveResult moveResult = tryMove(startPos, direction);
		if (moveResult instanceof MoveResult.Valid.Alive alive) {
			gameBoard.setCell(startPos, Cell.empty());
			final Position endPos = alive.newPosition();
			gameBoard.setCell(endPos, new EntityCell(gameBoard.getPlayer()));
			for (Position gemPos : alive.collectedGems()) {
				gameBoard.setCell(gemPos, Cell.empty());
				gameBoard.incrementGemCount();
			}
			for (Position lifePos : alive.collectedExtraLives()) {
				gameBoard.setCell(lifePos, Cell.empty());
				gameBoard.incrementLives();
			}
		}
		return moveResult;
	}

	/**
	 * Undoes a move by reverting all changes performed by the specified move. <p> Hint: Undoing a move is effectively the same as reversing everything you have done to make a move. </p>
	 * @param prevMove  The  {@link MoveResult}  object to revert.
	 */
	public void undoMove(final MoveResult prevMove) {
		Objects.requireNonNull(prevMove, "prevMove must not be null");
		if (prevMove instanceof MoveResult.Valid alive) {
			Position originalPos = alive.startPosition();
			Position newPos = alive.newPosition();
			gameBoard.setCell(newPos, Cell.empty());
			for (Position gemPos : alive.collectedGems()) {
				gameBoard.setCell(gemPos, new EntityCell(new Gem()));
				gameBoard.decrementGemCount();
			}
			for (Position lifePos : alive.collectedExtraLives()) {
				gameBoard.setCell(lifePos, new EntityCell(new ExtraLife()));
				gameBoard.decrementLives();
			}
			gameBoard.setCell(originalPos, new EntityCell(gameBoard.getPlayer()));
		} else if (prevMove instanceof MoveResult.Valid.Dead dead) {
			Position originalPos = dead.startPosition();
			Position newPos = dead.newPosition();
			gameBoard.setCell(newPos, new EntityCell(dead.mine()));
			gameBoard.setCell(originalPos, new EntityCell(gameBoard.getPlayer()));
			gameBoard.decrementLives();
		}
	}
}
