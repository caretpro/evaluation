
package pa1.controller;

import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
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
		this.gameState = gameState;
	}

	/**
	 * Processes a Move action performed by the player.
	 *
	 * @param direction The direction the player wants to move to.
	 * @return An instance of {@link MoveResult} indicating the result of the action.
	 */
	public MoveResult processMove(final Direction direction) {
		Objects.requireNonNull(direction, "direction cannot be null");
		try {
			Method moveMethod = gameState.getClass().getMethod("move", Direction.class);
			return (MoveResult) moveMethod.invoke(gameState, direction);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("GameState must implement a 'move' method with Direction parameter", e);
		} catch (Exception e) {
			throw new RuntimeException("Error invoking 'move' method on GameState", e);
		}
	}

	/**
	 * Processes an Undo action performed by the player.
	 *
	 * @return {@code false} if there are no steps to undo.
	 */
	public boolean processUndo() {
		try {
			Method undoMethod = gameState.getClass().getMethod("undo");
			return (boolean) undoMethod.invoke(gameState);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("GameState must implement an 'undo' method", e);
		} catch (Exception e) {
			throw new RuntimeException("Error invoking 'undo' method on GameState", e);
		}
	}
}