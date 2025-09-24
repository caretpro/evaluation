
package pa1.controller;

import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;
import org.jetbrains.annotations.NotNull;

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
        this.gameState = java.util.Objects.requireNonNull(gameState, "gameState cannot be null");
    }

    /**
     * Processes a Move action performed by the player.
     *
     * @param direction The direction the player wants to move to.
     * @return An instance of {@link MoveResult} indicating the result of the action.
     */
    public MoveResult processMove(final Direction direction) {
        // Save current state for undo
        gameState.saveState();

        // Attempt to move in the specified direction
        boolean moved = gameState.tryMove(direction);

        if (moved) {
            // Return a successful move result
            return new MoveResult(true, "Moved " + direction);
        } else {
            // Revert to previous state if move failed
            gameState.undo();
            return new MoveResult(false, "Cannot move " + direction);
        }
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        if (gameState.canUndo()) {
            gameState.undo();
            return true;
        }
        return false;
    }
}