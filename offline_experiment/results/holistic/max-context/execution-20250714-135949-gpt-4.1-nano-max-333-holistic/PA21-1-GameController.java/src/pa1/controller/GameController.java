
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
        // Initialize the gameState field with the provided instance
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Processes a Move action performed by the player.
     *
     * @param direction The direction the player wants to move to.
     * @return An instance of {@link MoveResult} indicating the result of the action.
     */
    public MoveResult processMove(final Direction direction) {
        // Attempt to perform the move using the move stack
        boolean moveSucceeded = gameState.getMoveStack().push(direction);
        if (moveSucceeded) {
            // Update game state after successful move
            gameState.incrementNumMoves();
            // Additional game logic can be added here
            return MoveResult.SUCCESS;
        } else {
            // Move was invalid or blocked
            return MoveResult.INVALID;
        }
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        // Attempt to undo the last move
        boolean undoSucceeded = gameState.getMoveStack().pop();
        if (undoSucceeded) {
            // Decrement move count or handle other undo logic if needed
            return true;
        } else {
            // No moves to undo
            return false;
        }
    }
}