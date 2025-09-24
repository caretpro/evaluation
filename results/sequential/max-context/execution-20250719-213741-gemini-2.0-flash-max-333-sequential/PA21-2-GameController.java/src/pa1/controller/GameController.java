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
		this.gameState = Objects.requireNonNull(gameState, "gameState cannot be null");
	}

	/**
	 * Processes a Move action performed by the player.
	 * @param direction  The direction the player wants to move to.
	 * @return  An instance of  {@link MoveResult}  indicating the result of the action.
	 */
	public MoveResult processMove(final Direction direction) {
		Objects.requireNonNull(direction, "direction cannot be null");
		final var gameBoardController = gameState.getGameBoardController();
		final var moveResult = gameBoardController.movePlayer(direction);
		if (moveResult.isValidMove()) {
			gameState.incrementNumMoves();
			gameState.getMoveStack().push(direction);
			if (moveResult.isDead()) {
				gameState.incrementNumDeaths();
				if (!gameState.hasUnlimitedLives()) {
					try {
						gameState.decrementNumLives();
					} catch (RuntimeException e) {
						return moveResult;
					}
				}
			}
		}
		return moveResult;
	}

	/**
	 * Processes an Undo action performed by the player.
	 * @return   {@code  false}  if there are no steps to undo.
	 */
	public boolean processUndo() {
		if (gameState.getMoveStack().isEmpty()) {
			return false;
		}
		Direction lastMove = gameState.getMoveStack().pop();
		if (lastMove == null) {
			return false;
		}
		final var gameBoardController = gameState.getGameBoardController();
		gameBoardController.undoMove(lastMove);
		gameState.decreaseNumMoves();
		return true;
	}
}
