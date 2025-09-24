
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
        this.gameState = Objects.requireNonNull(gameState, "gameState cannot be null");
    }

    /**
     * Processes a Move action performed by the player.
     *
     * @param direction The direction the player wants to move to.
     * @return An instance of {@link MoveResult} indicating the result of the action.
     */
    public MoveResult processMove(final Direction direction) {
        // Save current state for undo
        gameState.save();

        // Attempt to move in the specified direction
        boolean moved = gameState.move(direction);

        if (moved) {
            // Return success result
            return MoveResult.success("Moved " + direction);
        } else {
            // If move failed, revert to previous state
            gameState.undo();
            return MoveResult.failure("Cannot move " + direction);
        }
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        // Attempt to undo the last move
        return gameState.undo();
    }
}