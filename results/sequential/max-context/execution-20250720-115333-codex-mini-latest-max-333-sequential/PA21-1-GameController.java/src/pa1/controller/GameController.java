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
		Objects.requireNonNull(direction, "direction must not be null");
		var boardCtrl = gameState.getGameBoardController();
		var offset = direction.getOffset();
		var boardResult = boardCtrl.movePlayer(offset);
		if (boardResult == MoveResult.INVALID) {
			return MoveResult.INVALID;
		}
		gameState.getMoveStack().push(new Move(direction));
		gameState.incrementNumMoves();
		if (boardResult == MoveResult.GEM) {
			return MoveResult.GEM;
		}
		if (boardResult == MoveResult.DEATH) {
			gameState.decrementNumLives();
			gameState.incrementNumDeaths();
			return MoveResult.DEATH;
		}
		return MoveResult.VALID;
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
		MoveStack.Move lastMove = stack.pop();
		gameState.getGameBoardController().movePlayer(lastMove.direction().getOpposite().getOffset());
		return true;
	}
}
