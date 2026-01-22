
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
        boolean moveSuccessful = gameBoard.movePlayer(direction);

        if (moveSuccessful) {
            // Record the move
            gameState.getMoveStack().push(direction);
            gameState.incrementNumMoves();

            // Check if the player collected a gem
            boolean gemCollected = gameBoard.playerCollectedGem();

            // Check if the game has been won
            boolean hasWon = gameState.hasWon();

            // Return a concrete MoveResult instance
            return new MoveResultImpl(moveSuccessful, gemCollected, hasWon);
        } else {
            // Invalid move
            return new MoveResultImpl(false, false, false);
        }
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

        // Undo the last move
        Direction lastDirection = gameState.getMoveStack().pop();

        // Determine opposite direction
        Direction oppositeDirection = switch (lastDirection) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };

        boolean moveUndone = gameBoard.movePlayer(oppositeDirection);

        if (moveUndone) {
            // Undo does not count as a move
            return true;
        } else {
            // Revert the pop if move back is invalid
            gameState.getMoveStack().push(lastDirection);
            return false;
        }
    }

    /**
     * Concrete implementation of MoveResult.
     */
    private static class MoveResultImpl extends MoveResult {
        private final boolean moveSucceeded;
        private final boolean gemCollected;
        private final boolean gameWon;

        public MoveResultImpl(boolean moveSucceeded, boolean gemCollected, boolean gameWon) {
            super(); // No-arg constructor if exists; if not, remove this line
            this.moveSucceeded = moveSucceeded;
            this.gemCollected = gemCollected;
            this.gameWon = gameWon;
        }

        @Override
        public boolean moveSucceeded() {
            return moveSucceeded;
        }

        @Override
        public boolean gemCollected() {
            return gemCollected;
        }

        @Override
        public boolean gameWon() {
            return gameWon;
        }
    }
}