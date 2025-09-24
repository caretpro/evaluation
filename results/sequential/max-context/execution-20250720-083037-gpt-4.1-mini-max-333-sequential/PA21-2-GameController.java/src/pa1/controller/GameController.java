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
		var gameBoardController = gameState.getGameBoardController();
		var moveStack = gameState.getMoveStack();
		MoveResult moveResult = gameBoardController.movePlayer(direction);
		if (!moveResult.isValid()) {
			return moveResult;
		}
		moveStack.push(direction);
		gameState.incrementNumMoves();
		if (moveResult.isDeath()) {
			gameState.incrementNumDeaths();
			gameState.decrementNumLives();
		}
		if (gameState.hasWon()) {
			return MoveResult.won();
		}
		if (gameState.hasLost()) {
			return MoveResult.lost();
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
		var lastMoveResult = moveStack.pop();
		var gameBoardController = gameState.getGameBoardController();
		if (lastMoveResult instanceof MoveResult.Valid validMove) {
			boolean undone = gameBoardController.undoLastMove(validMove.origPosition);
			if (!undone) {
				moveStack.push(lastMoveResult);
				return false;
			}
			return true;
		}
		return false;
	}
}
