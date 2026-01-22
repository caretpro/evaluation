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
		this.gameState = Objects.requireNonNull(gameState, "gameState must not be null");
	}

	/**
	 * Processes a Move action performed by the player.
	 * @param direction  The direction the player wants to move to.
	 * @return  An instance of  {@link MoveResult}  indicating the result of the action.
	 */
	public MoveResult processMove(final Direction direction) {
		Objects.requireNonNull(direction, "direction must not be null");
		int playerX = gameState.getPlayerX();
		int playerY = gameState.getPlayerY();
		int newX = playerX;
		int newY = playerY;
		switch (direction) {
		case UP:
			newY--;
			break;
		case DOWN:
			newY++;
			break;
		case LEFT:
			newX--;
			break;
		case RIGHT:
			newX++;
			break;
		}
		if (gameState.isValidMove(newX, newY)) {
			gameState.setPlayerPosition(newX, newY);
			if (gameState.isGoalPosition(newX, newY)) {
				return MoveResult.SUCCESS;
			}
			return MoveResult.CONTINUE;
		}
		return MoveResult.FAILURE;
	}

	/**
	 * Processes an Undo action performed by the player.
	 * @return   {@code  false}  if there are no steps to undo.
	 */
	public boolean processUndo() {
		if (previousX == -1 || previousY == -1) {
			return false;
		}
		int currentX = gameState.getPlayerX();
		int currentY = gameState.getPlayerY();
		gameState.setPlayerPosition(previousX, previousY);
		previousX = currentX;
		previousY = currentY;
		return true;
	}
}
