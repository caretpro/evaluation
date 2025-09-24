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
	 * Processes an Undo action performed by the player.
	 *
	 * @return {@code false} if there are no steps to undo.
	 */
	public boolean processUndo() {
		// TODO
		return false;
	}

	/**
	 * Creates an instance.
	 * @param gameState  The instance of  {@link GameState}  to control.
	 */
	public void GameController(final GameState gameState) {
		this.gameState = Objects.requireNonNull(gameState, "gameState must not be null");
	}

	/**
	 * Processes a Move action performed by the player.
	 * @param direction  The direction the player wants to move to.
	 * @return  An instance of  {@link MoveResult}  indicating the result of the action.
	 */
	public MoveResult processMove(final Direction direction) {
		Objects.requireNonNull(direction, "direction must not be null");
		gameState.pushUndoState();
		var currentPos = gameState.getPlayerPosition();
		var nextPos = currentPos.translate(direction);
		while (gameState.canMoveTo(nextPos)) {
			currentPos = nextPos;
			nextPos = currentPos.translate(direction);
		}
		gameState.setPlayerPosition(currentPos);
		var blocker = gameState.objectAt(nextPos).orElse(null);
		if (blocker == null) {
			return MoveResult.hitWall();
		} else if (blocker.isGem()) {
			gameState.collectGemAt(nextPos);
			return MoveResult.collectedGem();
		} else if (blocker.isGoal()) {
			return MoveResult.wonGame();
		} else {
			return MoveResult.hitWall();
		}
	}
}
