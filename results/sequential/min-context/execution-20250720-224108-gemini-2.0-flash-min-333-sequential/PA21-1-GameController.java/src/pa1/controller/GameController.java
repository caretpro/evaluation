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

	/**
	 * Creates an instance.
	 * @param gameState  The instance of  {@link GameState}  to control.
	 */
	public void GameController(final GameState gameState) {
		this.gameState = Objects.requireNonNull(gameState);
	}

	/**
	 * Processes a Move action performed by the player.
	 * @param direction  The direction the player wants to move to.
	 * @return  An instance of  {@link MoveResult}  indicating the result of the action.
	 */
	public MoveResult processMove(final Direction direction) {
		Objects.requireNonNull(direction);
		if (gameState.isGameOver()) {
			return new MoveResult(false, "Game is over.");
		}
		if (!gameState.canMove(direction)) {
			return new MoveResult(false, "Cannot move in that direction.");
		}
		gameState.move(direction);
		if (gameState.isGameOver()) {
			return new MoveResult(true, "Moved " + direction + ". Game Over!");
		}
		return new MoveResult(true, "Moved " + direction + ".");
	}

	/**
	 * Processes an Undo action performed by the player.
	 * @return   {@code  false}  if there are no steps to undo.
	 */
	public boolean processUndo() {
		try {
			Method canUndoMethod = gameState.getClass().getMethod("canUndo");
			boolean canUndo = (boolean) canUndoMethod.invoke(gameState);
			if (!canUndo) {
				return false;
			}
			Method undoMethod = gameState.getClass().getMethod("undo");
			undoMethod.invoke(gameState);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
