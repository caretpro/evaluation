
package pa1.controller;

import pa1.model.Direction;
import pa1.model.GameState;
import pa1.model.MoveResult;
import pa1.model.Position;
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

        // Get current position of the player
        Position currentPosition = gameBoard.getPlayerPosition();

        var offset = direction.getOffset();
        int newRow = currentPosition.getRow() + offset.getRowOffset();
        int newCol = currentPosition.getCol() + offset.getColOffset();

        if (!gameBoard.isWithinBounds(newRow, newCol)) {
            // Invalid move: out of bounds
            return new MoveResult(false, false, false);
        }

        if (gameBoard.isBlocked(newRow, newCol)) {
            // Invalid move: blocked
            return new MoveResult(false, false, false);
        }

        // Save current position for undo
        gameState.getMoveStack().push(currentPosition);

        // Move the player
        gameBoard.setPlayerPosition(newRow, newCol);

        boolean gemCollected = false;
        boolean gameWon = false;
        boolean gameLost = false;

        if (gameBoard.hasGem(newRow, newCol)) {
            gameBoard.removeGem(newRow, newCol);
            gemCollected = true;
        }

        // Increment move count
        gameState.incrementNumMoves();

        if (gameState.hasWon()) {
            gameWon = true;
        }

        if (gameState.hasLost()) {
            gameLost = true;
        }

        return new MoveResult(true, gemCollected, gameWon || gameLost);
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

        Position previousPosition = gameState.getMoveStack().pop();

        var gameBoard = gameState.getGameBoard();

        // Move player back to previous position
        gameBoard.setPlayerPosition(previousPosition.getRow(), previousPosition.getCol());

        // Increment move count
        gameState.incrementNumMoves();

        // Undo points deduction can be handled externally if needed
        return true;
    }
}