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
		boolean moveSuccessful = gameState.movePlayer(direction);
		if (moveSuccessful) {
			return MoveResult.success();
		} else {
			return MoveResult.failure();
		}
	}

	public boolean processUndo() {
		return gameState.undo();
	}
}
