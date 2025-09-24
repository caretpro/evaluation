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
		boolean moveSuccess = boardController.move(direction);
		if (moveSuccess) {
			gameState.incrementNumMoves();
			gameState.getMoveStack().push(direction);
			if (boardController.hasPlayerDied()) {
				gameState.incrementNumDeaths();
				gameState.decrementNumLives();
				return MoveResult.DEATH;
			}
			if (gameState.hasWon()) {
				return MoveResult.WIN;
			}
			return MoveResult.SUCCESS;
		} else {
			return MoveResult.INVALID;
		}
	}

	/**
	 * Processes an Undo action performed by the player.
	 * @return   {@code  false}  if there are no steps to undo.
	 */
	public boolean processUndo() {
		if (gameState.getMoveStack().isEmpty()) {
			return false;
		}
		MoveResult lastMove = gameState.getMoveStack().pop();
		GameBoardController boardController = gameState.getGameBoardController();
		boardController.undoMove(lastMove);
		return true;
	}
}
