
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
        var gameBoard = gameState.getGameBoard();

        // Attempt to move the player in the specified direction
        boolean moveSuccessful = false;
        try {
            // Assuming the method is named tryMovePlayer and returns boolean
            moveSuccessful = gameBoard.tryMovePlayer(direction);
        } catch (UnsupportedOperationException e) {
            return MoveResult.failure("Move not supported");
        }

        if (!moveSuccessful) {
            return MoveResult.failure("Move blocked");
        }

        // Move was successful, update move stack
        gameState.getMoveStack().push(direction);

        // Increment move count
        gameState.incrementNumMoves();

        return MoveResult.success();
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

        Direction lastDirection = gameState.getMoveStack().pop();

        Direction oppositeDirection = switch (lastDirection) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };

        boolean moveReverted = false;
        try {
            moveReverted = gameState.getGameBoard().tryMovePlayer(oppositeDirection);
        } catch (UnsupportedOperationException e) {
            // Push back if undo not supported
            gameState.getMoveStack().push(lastDirection);
            return false;
        }

        if (!moveReverted) {
            // Push back if revert failed
            gameState.getMoveStack().push(lastDirection);
            return false;
        }

        // Decrement move count
        gameState.decrementNumMoves();

        return true;
    }
}