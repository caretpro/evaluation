
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
        // perform the move on the board and record it
        MoveResult result = gameState.getGameBoardController().movePlayer(direction);
        // record every attempted move
        gameState.getMoveStack().push(result);
        // if move was valid, increment move counter
        if (result.isValidMove()) {
            gameState.incrementNumMoves();
        }
        // if move caused death, increment death counter and decrement a life
        if (result == MoveResult.DEAD) {
            gameState.incrementNumDeaths();
            gameState.decrementNumLives();
        }
        return result;
    }

    /**
     * Processes an Undo action performed by the player.
     *
     * @return {@code false} if there are no steps to undo.
     */
    public boolean processUndo() {
        // if no moves to undo, fail
        if (gameState.getMoveStack().popCount() == 0) {
            return false;
        }
        // pop last move result and rollback board state
        MoveResult last = gameState.getMoveStack().pop();
        gameState.getGameBoardController().undoMove(last);
        return true;
    }
}