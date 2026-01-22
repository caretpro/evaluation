
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
        var moveResult = boardController.move(direction);
        
        if (moveResult.getType() != MoveResult.MoveResultType.INVALID) {
            gameState.incrementNumMoves();
            gameState.getMoveStack().push(direction);
            
            if (moveResult.getType() == MoveResult.MoveResultType.DEATH) {
                gameState.incrementNumDeaths();
                gameState.decrementNumLives();
            }
        }
        
        return moveResult;
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
        
        var lastDirection = moveStack.pop();
        var boardController = gameState.getGameBoardController();
        boardController.undo(lastDirection);
        
        return true;
    }
}