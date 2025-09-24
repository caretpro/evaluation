
package pa1.controller;

import org.jetbrains.annotations.NotNull;
import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;

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
        this.gameState = Objects.requireNonNull(gameState);
    }

    /**
     * Processes a Move action performed by the player.
     *
     * @param direction The direction the player wants to move to.
     * @return An instance of {@link MoveResult} indicating the result of the action.
     */
    public MoveResult processMove(final Direction direction) {
        Objects.requireNonNull(direction);

        // Perform the move on the board at the player's current position.
        // MoveResult tells us if it was INVALID, SUCCESS, COLLECTED_GEM, or KILLED_PLAYER.
        MoveResult result = gameState
            .getGameBoardController()
            .move(gameState.getGameBoard().getPlayerPosition(), direction);

        // Record every attempted move (valid or invalid).
        gameState.getMoveStack().push(direction);

        // Deduct one move point for every attempted move.
        gameState.incrementNumMoves();

        // On death, adjust lives and death count
        if (result == MoveResult.KILLED_PLAYER) {
            gameState.decrementNumLives();
            gameState.incrementNumDeaths();
        }

        return result;
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        if (gameState.getMoveStack().isEmpty()) {
            return false;
        }

        // Pop the last direction and undo it on the board from the player's current position.
        Direction last = gameState.getMoveStack().pop();
        MoveResult undone = gameState
            .getGameBoardController()
            .undo(gameState.getGameBoard().getPlayerPosition(), last);

        // An undo itself counts as two point deductions (handled by GameState.getScore()).
        // If we reversed a death, restore life and decrement death count.
        if (undone == MoveResult.KILLED_PLAYER) {
            gameState.increaseNumLives(1);
            gameState.decrementNumDeaths();
        }

        return true;
    }
}