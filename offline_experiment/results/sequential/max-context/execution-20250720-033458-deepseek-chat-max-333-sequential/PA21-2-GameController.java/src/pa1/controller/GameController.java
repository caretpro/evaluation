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
		GameBoardController boardController = gameState.getGameBoardController();
		boolean isValidMove = boardController.movePlayer(direction);
		if (!isValidMove) {
			return MoveResult.INVALID;
		}
		gameState.incrementNumMoves();
		gameState.getMoveStack().push(direction);
		if (boardController.hasPlayerDied()) {
			gameState.incrementNumDeaths();
			gameState.decrementNumLives();
			if (gameState.hasLost()) {
				return MoveResult.DEATH_GAME_OVER;
			}
			return MoveResult.DEATH;
		}
		if (gameState.hasWon()) {
			return MoveResult.WIN;
		}
		return MoveResult.VALID;
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
		GameBoardController boardController = gameState.getGameBoardController();
		MoveResult moveResult = boardController.makeMove(lastMove);
		boardController.undoMove(moveResult);
		gameState.incrementNumMoves(-1);
		return true;
	}
}
