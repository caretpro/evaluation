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
		this.gameState = Objects.requireNonNull(gameState, "gameState");
	}

	/**
	 * Processes a Move action performed by the player.
	 * @param direction  The direction the player wants to move to.
	 * @return  An instance of  {@link MoveResult}  indicating the result of the action.
	 */
	public MoveResult processMove(final Direction direction) {
		Objects.requireNonNull(direction, "direction");
		var boardCtrl = gameState.getGameBoardController();
		var preMoveGems = gameState.getNumGems();
		var moved = boardCtrl.move(direction.getOffset());
		if (!moved) {
			return MoveResult.invalid();
		}
		gameState.incrementNumMoves();
		gameState.getMoveStack().push(direction);
		var gemsCollected = preMoveGems - gameState.getNumGems();
		boolean died = boardCtrl.isOnHazard();
		if (died) {
			gameState.decrementNumLives();
			gameState.incrementNumDeaths();
		}
		if (gameState.hasWon()) {
			return MoveResult.win(gemsCollected, gameState.getNumLives(), gameState.getScore());
		}
		if (gameState.hasLost()) {
			return MoveResult.lose(gemsCollected, gameState.getNumLives(), gameState.getScore());
		}
		return MoveResult.moved(gemsCollected, gameState.getNumLives(), gameState.getScore());
	}

	/**
	 * Processes an Undo action performed by the player.
	 * @return   {@code  false}  if there are no steps to undo.
	 */
	public boolean processUndo() {
		var stack = gameState.getMoveStack();
		if (stack.isEmpty()) {
			return false;
		}
		Direction last = stack.popMove();
		gameState.getGameBoardController().undo(last);
		gameState.incrementNumMoves();
		return true;
	}
}
