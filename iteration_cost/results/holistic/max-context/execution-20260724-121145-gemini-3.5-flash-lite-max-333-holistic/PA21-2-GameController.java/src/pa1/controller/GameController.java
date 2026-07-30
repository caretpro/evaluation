
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
	 *
	 * @param gameState The instance of {@link GameState} to control.
	 */
	public GameController(final GameState gameState) {
		this.gameState = Objects.requireNonNull(gameState);
	}

	/**
	 * Processes a Move action performed by the player.
	 *
	 * @param direction The direction the player wants to move to.
	 * @return An instance of {@link MoveResult} indicating the result of the action.
	 */
	public MoveResult processMove(final Direction direction) {
		if (gameState.hasWon() || gameState.hasLost()) {
			return null;
		}

		final GameBoardController boardController = gameState.getGameBoardController();
		final MoveResult result = boardController.executeMove(direction);

		if (result != null) {
			gameState.incrementNumMoves();
			if (result.died()) {
				gameState.incrementNumDeaths();
				if (!gameState.hasUnlimitedLives()) {
					gameState.decrementNumLives();
				}
			}
		}

		return result;
	}

	/**
	 * Processes an Undo action performed by the player.
	 *
	 * @return {@code false} if there are no steps to undo.
	 */
	public boolean processUndo() {
		if (gameState.getMoveStack().isEmpty()) {
			return false;
		}

		final GameBoardController boardController = gameState.getGameBoardController();
		return boardController.undo();
	}
}