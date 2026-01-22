
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
        this.gameState = gameState;
    }

    /**
     * Processes a Move action performed by the player.
     *
     * @param direction The direction the player wants to move to.
     * @return An instance of {@link MoveResult} indicating the result of the action.
     */
    public MoveResult processMove(final Direction direction) {
        var boardController = gameState.getGameBoardController();
        // Use the public move method that takes only Direction
        MoveResult result = boardController.move(direction);

        if (result.wasValid()) {
            gameState.incrementNumMoves();

            if (result.wasDeath()) {
                gameState.incrementNumDeaths();
                if (!gameState.hasUnlimitedLives()) {
                    gameState.decrementNumLives();
                }
            }

            // Record the move in the move stack for undo functionality
            gameState.getMoveStack().push(result);
        }

        return result;
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        var moveStack = gameState.getMoveStack();
        if (moveStack.isEmpty()) {
            return false;
        }

        MoveResult lastMove = moveStack.pop();
        var boardController = gameState.getGameBoardController();
        boardController.undoMove(lastMove);

        // Adjust deaths and lives if last move was a death
        if (lastMove.wasDeath()) {
            // Cannot decrement deaths count due to no setter, rely on scoring adjustments.
            if (!gameState.hasUnlimitedLives()) {
                gameState.increaseNumLives(1);
            }
        }

        return true;
    }
}