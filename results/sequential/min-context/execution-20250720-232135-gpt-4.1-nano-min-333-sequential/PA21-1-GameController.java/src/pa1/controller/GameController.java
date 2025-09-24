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
		this.gameState = Objects.requireNonNull(gameState);
	}

	public MoveResult processMove(final Direction direction) {
		if (gameState.canMove(direction)) {
			gameState.movePlayer(direction);
			return MoveResult.success();
		} else {
			return MoveResult.failure();
		}
	}

	public boolean processUndo() {
		if (gameState.canUndo()) {
			gameState.undo();
			return true;
		}
		return false;
	}
}
