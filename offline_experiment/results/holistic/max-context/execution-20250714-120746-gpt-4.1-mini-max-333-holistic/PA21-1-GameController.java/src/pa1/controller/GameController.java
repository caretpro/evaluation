
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

        var boardController = gameState.getGameBoardController();
        MoveResult result = boardController.move(direction);

        if (result.isValid()) {
            gameState.incrementNumMoves();

            if (result.isPlayerDead()) {
                gameState.incrementNumDeaths();
                if (!gameState.hasUnlimitedLives()) {
                    gameState.decrementNumLives();
                }
            }

            // Record the move in the move stack for undo
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

        // Undo the move on the board
        boardController.undoMove(lastMove);

        // Adjust game state accordingly
        // Undo deducts 2 points, so moves count remains same or is handled in score calculation
        // Decrement deaths if player died in that move and restore lives if limited
        if (lastMove.isPlayerDead()) {
            // Since GameState has no decrement methods, we cannot decrement deaths or moves
            // But we can restore lives if limited
            if (!gameState.hasUnlimitedLives()) {
                gameState.increaseNumLives(1);
            }
        }

        return true;
    }
}