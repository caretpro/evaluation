package pa1.controller;

import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Controller for {@link pa1.InertiaTextGame}.
 *
 * <p>
 * All game state mutations should be performed by this class.
 * </p>
 */
public class GameController {

	@NotNull
	private final GameState gameState;

	public void GameController(final GameState gameState) {
		this.gameState = Objects.requireNonNull(gameState, "gameState must not be null");
	}

	public MoveResult processMove(final Direction direction) {
		Objects.requireNonNull(direction, "direction must not be null");
		var boardController = gameState.getGameBoardController();
		var moveResult = boardController.movePlayer(direction);
		if (moveResult == MoveResult.INVALID) {
			return MoveResult.INVALID;
		}
		gameState.incrementNumMoves();
		gameState.getMoveStack().push(direction);
		if (moveResult == MoveResult.DEATH) {
			gameState.incrementNumDeaths();
			if (!gameState.hasUnlimitedLives()) {
				gameState.decrementNumLives();
			}
			if (gameState.hasLost()) {
				return MoveResult.LOSE;
			}
			return MoveResult.DEATH;
		}
		if (gameState.hasWon()) {
			return MoveResult.WIN;
		}
		return moveResult;
	}

	/**
	 * Processes an Undo action performed by the player.
	 * @return   {@code  false}  if there are no steps to undo.
	 */
	public boolean processUndo() {
		var moveStack = gameState.getMoveStack();
		if (moveStack.isEmpty()) {
			return false;
		}
		Direction lastMove = moveStack.pop();
		Direction oppositeDirection = switch (lastMove) {
		case UP:
			yield Direction.DOWN;
		case DOWN:
			yield Direction.UP;
		case LEFT:
			yield Direction.RIGHT;
		case RIGHT:
			yield Direction.LEFT;
		};
		var boardController = gameState.getGameBoardController();
		MoveResult undoResult = boardController.movePlayer(oppositeDirection);
		if (undoResult == MoveResult.DEATH) {
			gameState.incrementNumDeaths();
			if (!gameState.hasUnlimitedLives()) {
				gameState.decrementNumLives();
			}
			if (gameState.hasLost()) {
				return true;
			}
		}
		return true;
	}
}
