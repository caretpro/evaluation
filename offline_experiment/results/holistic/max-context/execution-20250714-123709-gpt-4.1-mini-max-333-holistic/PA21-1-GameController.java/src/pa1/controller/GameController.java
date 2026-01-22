
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

        var gameBoardController = gameState.getGameBoardController();

        // Try to move the player in the given direction
        boolean moveSucceeded = gameBoardController.tryMove(direction);

        if (!moveSucceeded) {
            // Return a MoveResult indicating invalid move (assuming a static factory method)
            return MoveResult.invalid();
        }

        // Move succeeded: push to move stack and increment moves
        gameState.getMoveStack().push(direction);
        gameState.incrementNumMoves();

        // Check if the move caused death (assuming GameState or GameBoardController can provide this info)
        boolean deathOccurred = gameBoardController.hasPlayerDied();

        if (deathOccurred) {
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
            return MoveResult.death();
        }

        return MoveResult.valid();
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

        Direction lastMove = gameState.getMoveStack().pop();
        var gameBoardController = gameState.getGameBoardController();

        boolean undoSucceeded = gameBoardController.undo(lastMove);

        return undoSucceeded;
    }
}